package stencil.adapters.java2D.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.interpreter.Interpreter;
import stencil.parser.tree.DynamicRule;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.*/
public final class DynamicUpdateTask implements UpdateTask {
	private final DynamicRule rule;
	private final DisplayLayer<Glyph2D> table;
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
	
	public DynamicUpdateTask(DisplayLayer<Glyph2D> table, DynamicRule rule) {
		this.table = table;
		this.rule = rule;
	}
		
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
	}
	

	public void conservativeUpdate() {if (needsUpdate()) {update();}}

	public boolean needsUpdate() {return rule.requiresUpdate();}

	public void update() {
		System.out.println("updating...");
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
	}	
}
