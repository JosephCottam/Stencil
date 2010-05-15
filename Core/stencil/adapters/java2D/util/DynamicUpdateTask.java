package stencil.adapters.java2D.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.interpreter.Interpreter;
import stencil.parser.tree.DynamicRule;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask implements UpdateTask {
	private final DynamicRule rule;
	private final DisplayLayer<Glyph2D> table;
	
	//TODO: Move the store data to the layer (makes this automatically respect deletes, can reduce the amount of data retained)
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
	
	//HACK:  Indicates that the cached data has changed, so the dynamic binding must be re-considered.
	//       This is required because scheduling between the loaders and dynamic updater is not guaranteed
	//       to present the layer store consistently with the operator state data.  Therefore, the state query
	//       is not always 100% accurate and can lead to missed updates that are never rectified (e.g. esp with finite
	//       streams).  This should be removed when coordination between render and load is properly implemented.
	private boolean forceUpdate = false; //TODO: Remove when the paint/load coordination is correct.
	
	public DynamicUpdateTask(DisplayLayer<Glyph2D> table, DynamicRule rule) {
		this.table = table;
		this.rule = rule;
	}
		
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
		forceUpdate = true;
	}

	public void conservativeUpdate() {if (needsUpdate()) {update();}}

	public boolean needsUpdate() {return rule.requiresUpdate() || forceUpdate;}

	public void update() {
		//TODO: Add support for Local!
		
		//Update each element 
		for (Glyph2D glyph: table) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			if (source == null) {continue;} 					//This dynamic updater does not apply to this glyph
			
			try {
				Tuple result = Interpreter.process(source, rule.getAction());
				Glyph2D newGlyph = glyph.update(result);
				if (glyph != newGlyph) {table.update(newGlyph);}
			}
			catch (Exception ex) {
				System.err.println("Error in dynamic update.");
				ex.printStackTrace();
			}			
		}
		forceUpdate = false;
	}	
}
