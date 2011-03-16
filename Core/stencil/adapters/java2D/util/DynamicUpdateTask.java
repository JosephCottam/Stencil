package stencil.adapters.java2D.util;

import java.util.List;

import stencil.interpreter.tree.DynamicRule;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask extends UpdateTask<DynamicRule> {
	private final DoubleBufferLayer table;
	
	public DynamicUpdateTask(DoubleBufferLayer table, DynamicRule rule) {
		//TODO: Re-arrange when dynamic rule is frozen
		super(rule, rule.action().prototype().toString());
		this.table = (DoubleBufferLayer) table;
	}
	
	public Finisher update() {
		final List<Tuple> result = viewpointFragment.apply(table);
		
		return new Finisher() {
			public void finish() {
				table.directUpdate(result);
			}
		};
	}
}
