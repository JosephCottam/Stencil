package stencil.adapters.java2D.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.interpreter.Interpreter;
import stencil.parser.tree.Rule;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.tuple.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.*/
public final class DynamicUpdateTask implements UpdateTask {
	private final Rule rule;
	private final DisplayLayer<Glyph2D> table;
	private final Map<String, Tuple> sourceData = new ConcurrentHashMap();
	
	public DynamicUpdateTask(DisplayLayer<Glyph2D> table, Rule rule) {
		this.table = table;
		this.rule = stencil.interpreter.DynamicRule.toDynamic(rule);  //TODO: Remove when dynamic rules are created in the compiler
	}
		
	public void addUpdate(Tuple sourceData, Glyph2D target) {
		this.sourceData.put(target.getID(), sourceData);
	}
	

	public void conservativeUpdate() {if (needsUpdate()) {update();}}

	//TODO: Query to see if this rule needs to be run (has something in the rule changed its state?)
	public boolean needsUpdate() {return true;}

	public void update() {
		//TODO: Add support for Local!
		
		//Update each element 
		for (Glyph2D glyph: table) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			if (source == null) {continue;} 					//This dynamic updater does not apply to this glyph
			
			try {
				Tuple result = Interpreter.process(source, rule);
				Glyph2D newGlyph = glyph.update(result);
				if (result != newGlyph) {table.update(newGlyph);}
			}
			catch (Exception ex) {
				System.err.println("Error in dynamic update.");
				ex.printStackTrace();
			}
			
		}		
	}	
}
