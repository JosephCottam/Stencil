package stencil.adapters.java2D.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.parser.tree.DynamicRule;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.display.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask extends UpdateTask {
	private final DynamicRule rule;
	private final DisplayLayer table;
	
	//TODO: Move the store data to the layer (makes this automatically respect deletes, can reduce the amount of data retained...super great if it were a column store!)
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
		
	public DynamicUpdateTask(DisplayLayer table, DynamicRule rule) {
		this.table = table;
		this.rule = rule;
	}
		
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
	}

	public boolean needsUpdate() {return rule.requiresUpdate();}

	public void update() {
		rule.apply(table, sourceData);
	}
	
	public String toString() {return "Dynamic update for " + table.getName() + ": " + rule.getAction().getTarget().getPrototype().toStringTree();}
}
