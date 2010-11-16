package stencil.interpreter;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.List;


import static java.lang.String.format;

import stencil.display.Display;
import stencil.display.IDException;
import stencil.display.StencilPanel;
import stencil.parser.ParserConstants;
import stencil.parser.tree.*;
import stencil.parser.tree.util.Environment;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleAppender;
import stencil.tuple.Tuples;
import stencil.tuple.instances.MapMergeTuple;
import stencil.types.Converter;

public class Interpreter {
	private final WeakReference<StencilPanel> panel;	//
	private final Program program;
	
	public static boolean abortOnError = false;
	
	public Interpreter(StencilPanel panel) {
		this.panel = new WeakReference(panel);
		this.program = panel.getProgram();
	}
	
	public static Tuple processSequential(Tuple streamTuple, Rule rule) throws NoOutputSignal, RuleAbortException {return processSequential(streamTuple, Arrays.asList(rule));}
	public static Tuple processSequential(Tuple streamTuple, Iterable<Rule> rules) throws NoOutputSignal, RuleAbortException {
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, streamTuple);
		return process(env, rules);
	}

			
	public static Tuple process(Environment env, Rule... rules) throws NoOutputSignal, RuleAbortException {return process(env, Arrays.asList(rules));}	
	public static Tuple process(Environment env, Iterable<Rule> rules) throws NoOutputSignal, RuleAbortException {
		if (rules == null || env == null) {return Tuples.EMPTY_TUPLE;}

		List<Tuple> resultBuffer = new ArrayList();
		
		//Apply all rules
		try {
			for (Rule rule: rules) {
				Tuple result;				
				try {result = rule.apply(env);}
				catch (RuleAbortException ra) {
					if (abortOnError) {throw new RuleAbortException(rule);}
					else {throw new NoOutputSignal("No output due to rule abort.", ra);}
				}
				catch (NoOutputSignal no) {throw no;}
				catch (Exception e) {throw new RuleAbortException(rule, e);}
				
				resultBuffer.add(result);
			}
			
		} catch (RuleAbortException ra) {
			System.out.println("Rule aborted...");
			System.out.println(ra.getMessage());
			ra.printStackTrace();
			return null;			//TODO: Handle rule aborts.
		}
		
		Tuple fullResult = TupleAppender.crossAppend(resultBuffer);
		
		return fullResult;
	}
	
	//Parallelize here???  Checks all groups in parallel...
	public Tuple process(SourcedTuple source, TupleStore target, Consumes group) throws NoOutputSignal, RuleAbortException {
		final String targetLabel = (target instanceof Layer) ? "Layer": "stream";
		boolean matches;
		Tuple prefilter, local;
		Tuple result = null;
		
		//TODO: replace EMPTY_TUPLE with globals tuple when runtime globals are added (be sure to look for other "getDefault" and fix them up too
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, source.getValues());
		
		prefilter = process(env, group.getPrefilterRules());
		env.setFrame(Environment.PREFILTER_FRAME, prefilter);
		
		try {matches = group.matches(env);}
		catch (Exception e) {throw new RuntimeException(format("Error processing predicates in layer %1$s", target.getName()), e);}				
		
		if (matches) {
			
			local = process(env, group.getLocalRules());				
			env.setFrame(Environment.LOCAL_FRAME, local);
			try {
				result = process(env, group.getResultRules());				
				if (result != null && result instanceof MapMergeTuple) {
					for (int i=0; i< result.size(); i++) {
						Tuple nt = Converter.toTuple(result.get(i));
						if (target.canStore(nt)) {target.store(nt);}
					}
				} else if (result != null && target.canStore(result)) {
					target.store(result);
				}

			}
			catch (NoOutputSignal no) {throw no;}
			catch (IDException IDex) {throw IDex;}
			catch (Exception e) {throw new RuntimeException(format("Error processing glyph rules in %1$s %2$s", targetLabel, target.getName()), e);}
			
			try {
				Tuple viewUpdate = process(env, group.getViewRules());
				if (viewUpdate != null) {Tuples.transfer(viewUpdate, Display.view);}
			}
			catch (Exception e) {throw new RuntimeException(format("Error processing view rules in %1$s %2$s", targetLabel, target.getName()), e);}
			
			try{
				Tuple canvasUpdate = process(env, group.getCanvasRules());
				if (canvasUpdate != null) {Tuples.transfer(canvasUpdate, Display.canvas);}
			} catch (Exception e) {throw new RuntimeException(format("Error processing canvas rules in %1$s %2$s", targetLabel, target.getName()), e);}
		}
		return result;
	}
	
	/**Takes a single tuple and works it through all applicable
	 * transformation contexts.  This will add the tuple to all layers 
	 * and generate all resulting stream tuples.  This does not drain
	 * the stream queues.
	 * 
	 * @param source
	 * @throws Exception
	 */
	private boolean processSingleTuple(SourcedTuple source) throws Exception {
		boolean actionsTaken = false;
		for (StreamDef stream: program.getStreamDefs()) {
			stream.offer(StreamDef.DIVIDER);
			for (Consumes group: stream.getGroups()) {
				if (!group.getStream().equals(source.getSource())) {continue;}
				try {
					process(source, stream, group);
					actionsTaken = true;
				} catch (NoOutputSignal no) {
					actionsTaken = true;
				}
			}
		}
		
		for (Layer layer:program.getLayers()) {			
			for(Consumes group:layer.getGroups()) {
				if (!group.getStream().equals(source.getSource())) {continue;}
				try {
					Tuple result = process(source, layer, group);
					actionsTaken = registerDynamics(layer, group, source, result) && actionsTaken;
				} catch (NoOutputSignal no) {
					actionsTaken = true;
				}
			}
		}
		return actionsTaken;
	}

	//TODO: What about locals in a dynamic binding?
	private boolean registerDynamics(Layer layer, Consumes group, SourcedTuple source, Tuple result) {
		boolean actionsTaken = false;
		if (result != null && result instanceof MapMergeTuple) {
			for (int i=0; i<result.size(); i++) {
				Tuple rslt = (Tuple) result.get(i);
				singleRegisterDynamic(layer, group, source, rslt);
			}
		} else if (result != null) {
			singleRegisterDynamic(layer, group, source, result);
		}		
		return actionsTaken;
	}
	
	private boolean singleRegisterDynamic(Layer layer, Consumes group, SourcedTuple source, Tuple result) {
		if (!layer.canStore(result)) {return false;}
		try {
			String id = Converter.toString(result.get(ParserConstants.GLYPH_ID_FIELD));
			stencil.display.Glyph glyph = layer.getDisplayLayer().find(id);
			assert glyph != null;
			
			layer.getDisplayLayer().addDynamic(group.groupID(), glyph, source.getValues());

			return true;
		} catch (Exception e) {
			throw new RuntimeException(format("Error updating layer %1$s with tuple %2$s", layer.getName(), result.toString()), e);
		}
	}
	
	public void processTuple(SourcedTuple source) throws Exception {
		processSingleTuple(source);

		
		while (!allEmpty()) {
			for (StreamDef stream: program.getStreamDefs()) {
				Tuple divider = stream.poll();		//Strip off dividers
				assert divider==StreamDef.DIVIDER;
				while (!stream.isEmpty() && !stream.isDivider()) {
					SourcedTuple t = stream.poll();
					processSingleTuple(t);
				}
			}
		}
	}
	
	/**Are the stencil-defined streams all drained?*/
	private boolean allEmpty() {
		for (StreamDef stream: program.getStreamDefs()) {
			if (!stream.isEmpty()) {return false;}
		}
		return true;
	}

	public StencilPanel getPanel() {return panel.get();}
}
