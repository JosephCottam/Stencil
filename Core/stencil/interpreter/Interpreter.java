package stencil.interpreter;

import java.lang.ref.WeakReference;


import static java.lang.String.format;

import stencil.display.Display;
import stencil.display.IDException;
import stencil.display.StencilPanel;
import stencil.interpreter.tree.*;
import stencil.parser.ParserConstants;
import stencil.parser.tree.util.Environment;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.types.Converter;
import static stencil.interpreter.tree.Rule.EMPTY_RULE;

public class Interpreter {
	private final WeakReference<StencilPanel> panel;	//
	private final Program program;
	
	public Interpreter(StencilPanel panel) {
		this.panel = new WeakReference(panel);
		this.program = panel.getProgram();
	}
	
	public static Tuple processTuple(Tuple streamTuple, Rule rule) throws RuleAbortException {
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, streamTuple);
		return processEnv(env, rule);
	}
	

	public static Tuple processEnv(Environment env, Rule rule) throws RuleAbortException {
		if (rule == null || rule == EMPTY_RULE || env == null) {return Tuples.EMPTY_TUPLE;}
		Tuple result = Tuples.EMPTY_TUPLE;
		
		//Apply all rules
		try {
			try {result = rule.apply(env);}
			catch (NoOutput.Signal s) {result=NoOutput.TUPLE;}
			catch (RuleAbortException ra) {throw ra;}
			catch (Exception e) {throw new RuleAbortException(rule, e);}
		} catch (RuleAbortException ra) {
			System.out.println("Rule aborted...");
			System.out.println(ra.getMessage());
			ra.printStackTrace();
			return null;			//TODO: Handle rule aborts.
		}
		return result;
	}
	
	
	private static final class ProcessResults {
		final Tuple local, result;
		public ProcessResults(Tuple local, Tuple result) {this.local = local;this.result = result;}
	}
	

	//Parallelize here???  Checks all groups in parallel...
	public ProcessResults process(SourcedTuple source, TupleStore target, Consumes group) throws RuleAbortException {
		final String targetLabel = (target instanceof Layer) ? "Layer": "stream";
		boolean matches;
		Tuple prefilter;
		Tuple local = null;
		Tuple result = null;
		
		//TODO: replace EMPTY_TUPLE with globals tuple when runtime globals are added (be sure to look for other "getDefault" and fix them up too
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, source.getValues());
		
		prefilter = processEnv(env, group.getPrefilterRule());
		env.setFrame(Environment.PREFILTER_FRAME, prefilter);
		
		try {matches = group.matches(env);}
		catch (Exception e) {throw new RuntimeException(format("Error processing predicates in layer %1$s", target.getName()), e);}				
		
		if (matches) {
			
			local = processEnv(env, group.getLocalRule());				
			env.setFrame(Environment.LOCAL_FRAME, local);
			try {
				result = processEnv(env, group.getResultRule());				
				if (result != null && result instanceof MapMergeTuple) {
					MapMergeTuple mmt = (MapMergeTuple) result;
					for (int i=0; i< mmt.size(); i++) {
						Tuple nt = mmt.getTuple(i);
						if (target.canStore(nt)) {target.store(nt);}
					}
				} else if (result != null && target.canStore(result)) {
					target.store(result);
				}

			}
			catch (IDException IDex) {throw IDex;}
			catch (Exception e) {throw new RuntimeException(format("Error processing glyph rules in %1$s %2$s", targetLabel, target.getName()), e);}
			
			try {
				Tuple viewUpdate = processEnv(env, group.getViewRule());
				if (viewUpdate != null) {Tuples.transfer(viewUpdate, Display.view);}
			}
			catch (Exception e) {throw new RuntimeException(format("Error processing view rules in %1$s %2$s", targetLabel, target.getName()), e);}
			
			try{
				Tuple canvasUpdate = processEnv(env, group.getCanvasRule());
				if (canvasUpdate != null) {Tuples.transfer(canvasUpdate, Display.canvas);}
			} catch (Exception e) {throw new RuntimeException(format("Error processing canvas rules in %1$s %2$s", targetLabel, target.getName()), e);}
		}
		return new ProcessResults(local, result);
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
		for (StreamDef stream: program.streamDefs()) {
			stream.offer(StreamDef.DIVIDER);
			for (Consumes group: stream.getGroups()) {
				if (!group.getStream().equals(source.getSource())) {continue;}
				actionsTaken = true;
				process(source, stream, group);
			}
		}
		
		for (Layer layer:program.layers()) {			
			for(Consumes group:layer.getGroups()) {
				if (!group.getStream().equals(source.getSource())) {continue;}
				ProcessResults results = process(source, layer, group);
				actionsTaken = registerDynamics(layer, group, source, results.local, results.result) && actionsTaken;
				actionsTaken = true;
			}
		}
		return actionsTaken;
	}

	private boolean registerDynamics(Layer layer, Consumes group, SourcedTuple source, Tuple local, Tuple result) {
		boolean actionsTaken = false;
		
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, source.getValues(), Tuples.EMPTY_TUPLE, local);
		Tuple merged = new ArrayTuple(TupleRef.resolveAll(group.getDynamicReducer(), env));
		
		if (result != null && result instanceof MapMergeTuple) {
			for (int i=0; i<result.size(); i++) {
				Tuple rslt = (Tuple) result.get(i);
				singleRegisterDynamic(layer, group, merged, rslt);
			}
		} else if (result != null) {
			singleRegisterDynamic(layer, group, merged, result);
		}		
		return actionsTaken;
	}
	
	private boolean singleRegisterDynamic(Layer layer, Consumes group, Tuple source, Tuple result) {
		if (!layer.canStore(result)) {return false;}
		try {
			String id = Converter.toString(result.get(ParserConstants.SELECTOR_FIELD));
			stencil.display.Glyph glyph = layer.getDisplayLayer().find(id);
			assert glyph != null;
			
			layer.getDisplayLayer().addDynamic(group.groupID(), glyph, source);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(format("Error updating layer %1$s with tuple %2$s", layer.getName(), result.toString()), e);
		}
	}
	
	public void processTuple(SourcedTuple source) throws Exception {
		processSingleTuple(source);

		
		while (!allEmpty()) {
			for (StreamDef stream: program.streamDefs()) {
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
		for (StreamDef stream: program.streamDefs()) {
			if (!stream.isEmpty()) {return false;}
		}
		return true;
	}

	public StencilPanel getPanel() {return panel.get();}
}
