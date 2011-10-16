package stencil.interpreter.tree;

import java.util.ArrayList;
import java.util.Map;

import stencil.display.DynamicBindSource;
import stencil.display.Glyph;
import stencil.interpreter.Environment;
import stencil.interpreter.UpdateableComposite;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

import java.util.List;

public class DynamicRule implements UpdateableComposite<DynamicRule> {
	private final int groupID;	//TODO: Convert to a string (for better error reporting)
	private final String layerName;
	private final CallChain chain;
	private final Target target;
	private final StateQuery query;
	
	public DynamicRule (String layerName, int groupID, Target target, CallChain chain, StateQuery query) {
		this.layerName = layerName;
		this.groupID = groupID;
		this.chain = chain;
		this.query = query;
		this.target = target;
	}
	
	public String layerName() {return layerName;}
	public int groupID() {return groupID;}
	public StateQuery stateQuery() {return query;}
	public Target target() {return target;}
	
	
	/**Execute the action and return a tuple with fields
	 * corresponding to the prototype and values from the action.
	 * 
	 * Execution occurs data-state style.
	 *
	 * @param source
	 * @return
	 */
	public List<Tuple> apply(DynamicBindSource<? extends Glyph> view) {
		java.util.List<Tuple> results = new ArrayList(view.size());
		Map<String, Tuple> sourceData = view.getSourceData();
		final int depth = chain.depth();
		
		List<Environment> envs = new ArrayList(view.size());
		for (Glyph glyph: view) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			Environment env = Environment.getDefault(false, Tuples.EMPTY_TUPLE, source);
			envs.add(env.ensureCapacity(false, env.size() + depth));
		}
		
		for (int target=0; target< depth; target++) {
			for (Environment env: envs) {
				Tuple result = chain.applyStep(target, env);
				env.extend(result);
			}
		}
		
		for (Environment env: envs) {
			Tuple result = chain.pack(env);
			results.add(result);
		}
		return results;
	}	
	
	public DynamicRule viewpoint() {
		return new DynamicRule(layerName, groupID, target, chain.viewpoint(), query.viewpoint());
	}
}
