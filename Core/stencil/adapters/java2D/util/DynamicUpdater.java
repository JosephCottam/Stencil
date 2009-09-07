package stencil.adapters.java2D.util;


import stencil.parser.tree.Rule;
import stencil.adapters.Glyph;
import stencil.streams.Tuple;

/**Executes a dynamic update rule on all relevant glyphs.*/
public final class DynamicUpdater extends Thread implements Stopable {
	boolean run =true;
	Rule rule;
	
	public DynamicUpdater(Rule rule) {
		this.rule = rule;
	}
	
	public void run() {
		while(run) {
			updateAll();
		}
	}
	
	private void updateAll() {
		//Clear all marks in the set
		//Iterate all glyphs in the associated table
		//Update all nodes that are associated with this thread 
		//Sweep all stored tuples not marked 
	}
	
	public void addUpdate(Tuple sourceData,Glyph target) {
		//Associate the glyph with this thread in some way (could be a ref to this thread)
		//Store the source data in a mark-sweep set
	}
	
	public void signalStop() {run = false;}
}
