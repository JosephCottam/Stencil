package stencil.adapters.java2D.util;

import java.util.List;

import stencil.interpreter.tree.DynamicRule;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.display.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask extends UpdateTask<DynamicRule> {
	private final DisplayLayer table;
	
	public DynamicUpdateTask(DisplayLayer table, DynamicRule rule) {
		//TODO: Re-arrange when dynamic rule is frozen
		super(rule, rule.getStateQuery(), rule.getAction().prototype().toString());
		this.table = table;
	}
	
	public Finisher update() {
		final List<Tuple> result = viewPointFragment.apply(table);
		
		return new Finisher() {
			public void finish() {
				((DoubleBufferLayer) table).directUpdate(result);
			}
		};
	}
}
