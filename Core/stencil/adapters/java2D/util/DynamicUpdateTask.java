package stencil.adapters.java2D.util;

import java.util.List;

import stencil.parser.tree.DynamicRule;
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
		super(rule);
		this.table = table;
	}
		
	public boolean needsUpdate() {return originalFragment.requiresUpdate();}

	public Finisher update() {
		final List<Tuple> result = viewPointFragment.apply(table);
		
		return new Finisher() {
			public void finish() {
				((DoubleBufferLayer) table).directUpdate(result);
			}
		};
	}
	
	public String toString() {return "Dynamic update for " + table.getName() + ": " + originalFragment.getAction().getTarget().getPrototype().toStringTree();}
}
