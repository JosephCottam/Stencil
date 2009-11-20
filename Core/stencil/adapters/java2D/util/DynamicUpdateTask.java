package stencil.adapters.java2D.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.parser.tree.Rule;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.*/
final class DynamicUpdateTask implements Runnable, Stopable {
	private boolean run =true;
	private final Rule rule;
	private final DisplayLayer<Glyph2D> table;
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
	
	public DynamicUpdateTask(DisplayLayer<Glyph2D> table, Rule rule) {
		this.table = table;
		this.rule = rule;
	}
	
	public void run() {
		while(run) {
			runOnce();
			Thread.yield();
		}
	}
	
	public void runOnce() {updateAll();}
			
	private void updateAll() {	
		//TODO: Query to see if this rule needs to be run (has something in the rule changed its state?)
		
		//Update each element 
		for (Glyph2D glyph: table) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			if (source == null) {continue;} 					//This dynamic updater does not apply to this glyph
			
			try {
				Tuple result = rule.apply(source);
				Glyph2D newGlyph = glyph.update(result);
				if (newGlyph != glyph) {
					table.update(newGlyph);
				}
			}
			catch (Exception ex) {
				System.err.println("Error in dynamic update.");
				ex.printStackTrace();
			}
			
		}
	}
	
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
	}
	
	public void signalStop() {run = false;}	
}
