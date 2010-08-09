package stencil.adapters.java2D.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.parser.tree.DynamicRule;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.display.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask extends UpdateTask<DynamicRule> {
	private final DisplayLayer table;
	
	//TODO: Move the store data to the layer (makes this automatically respect deletes, can reduce the amount of data retained...super great if it were a column store!)
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
		
	public DynamicUpdateTask(DisplayLayer table, DynamicRule rule) {
		super(rule);
		this.table = table;
	}
		
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
	}

	public boolean needsUpdate() {return originalFragment.requiresUpdate();}

	public Finisher update() {
		System.out.println("running update: " + toString());
		final List<Tuple> result = viewPointFragment.apply(table, sourceData);		
		return new Finisher() {
			public void finish() {
				System.out.println("finishing update: " + toString());
				((DoubleBufferLayer) table).directUpdate(result);
			}
		};
	}
	
	public String toString() {return "Dynamic update for " + table.getName() + ": " + originalFragment.getAction().getTarget().getPrototype().toStringTree();}
}
