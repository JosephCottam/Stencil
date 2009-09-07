package stencil.adapters.java2D.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.parser.tree.Rule;
import stencil.adapters.Glyph;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Table;
import stencil.streams.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.*/
public final class DynamicUpdater extends Thread implements Stopable {
	//Marked entry for storage in the mapping
	private static final class Entry {
		Tuple sourceData;
		boolean mark = true;
		
		Entry(Tuple sourceData) {
			this.sourceData = sourceData;
		}
	}
	
	private boolean run =true;
	private final Rule rule;
	private final Table<Glyph2D> table;
	private final Map<String, Entry> sourceData = new ConcurrentHashMap();
	private boolean iterationMark = false;
	
	public DynamicUpdater(Table<Glyph2D> table, Rule rule) {
		this.table = table;
		this.rule = rule;
	}
	
	public void run() {
		while(run) {
			updateAll();
		}
	}
	
	private void updateAll() {
		iterationMark = !iterationMark;
		
		//Update each element 
		for (Glyph2D g: table) {
			Entry e = sourceData.get(g.getID());		//Get associated source data
			if (e == null) {continue;} 					//This dynamic updater does not apply to this glyph
			e.mark = iterationMark;
			Tuple source = e.sourceData;
			
			try {
				Tuple result = rule.apply(source);
				Glyph2D newGlyph = g.update(result);
				if (newGlyph != g) {table.update(newGlyph);}
			}
			catch (Exception ex) {/*TODO: Do something...*/}
			
		}
		
		//Sweep all stored tuples not marked 
		for (Entry e: sourceData.values()) {
			if (e.mark != iterationMark) {sourceData.remove(e);}
		}
	}
	
	public void addUpdate(Tuple sourceData, Glyph target) {
		
		//Associate the glyph with this thread in some way (could be a ref to this thread)
		//Store the source data in a mark-sweep set
	}
	
	public void signalStop() {run = false;}	
}
