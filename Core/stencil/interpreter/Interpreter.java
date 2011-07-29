package stencil.interpreter;

import java.lang.ref.WeakReference;


import static java.lang.String.format;

import stencil.display.IDException;
import stencil.display.StencilPanel;
import stencil.interpreter.tree.*;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.MapMergeTuple;
import static stencil.interpreter.tree.Rule.EMPTY_RULE;

public class Interpreter {
	private final WeakReference<StencilPanel> panel;
	private final Program program;
	
	public Interpreter(StencilPanel panel) {
		this.panel = new WeakReference(panel);
		this.program = panel.getProgram();
	}
	
	public static Tuple processTuple(Tuple tuple, Rule rule) throws RuleAbortException {
		Environment env = Environment.getDefault(Tuples.EMPTY_TUPLE, tuple);
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
	
	

	//Parallelize here???  Checks all groups in parallel...
	public void process(SourcedTuple source, TupleStore target, Consumes group) throws RuleAbortException {
		final String targetLabel = (target instanceof Layer) ? "Layer": "stream";
		boolean matches;
		Tuple prefilter;
		Tuple local = null;
		Tuple result = null;
		
		//TODO: replace EMPTY_TUPLE with globals tuple when runtime globals are added (be sure to look for other "getDefault" and fix them up too
		Environment env = Environment.getDefault(Tuples.EMPTY_TUPLE, source.getValues());
		
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
						PrototypedTuple nt = (PrototypedTuple) mmt.getTuple(i);
						if (target.canStore(nt)) {target.store(nt);}
					}
				} else if (result != null && target.canStore(result)) {
					target.store((PrototypedTuple) result);
				}

			}
			catch (IDException IDex) {throw IDex;}
			catch (Exception e) {throw new RuntimeException(format("Error processing glyph rules in %1$s %2$s", targetLabel, target.getName()), e);}
		}
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
				process(source, layer, group);
				actionsTaken = true;
			}
		}
		
		for(Consumes group:program.view().getGroups()) {
			if (!group.getStream().equals(source.getSource())) {continue;}
			process(source, (TupleStore) program.view(), group);
			actionsTaken = true;
		}
		
		for(Consumes group:program.canvas().getGroups()) {
			if (!group.getStream().equals(source.getSource())) {continue;}
			process(source, (TupleStore) program.canvas(), group);
			actionsTaken = true;
		}

		
		return actionsTaken;
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
