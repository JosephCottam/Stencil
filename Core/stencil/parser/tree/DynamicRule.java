package stencil.parser.tree;


import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.interpreter.Interpreter;
import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;

public class DynamicRule extends StencilTree {
	public DynamicRule(Token token) {super(token);}

	public Rule getAction() {return (Rule) getChild(0);}
	
	/**What group does this rule belong to?*/
	public Consumes getGroup() {
		Tree t = this.getAncestor(StencilParser.CONSUMES);
		if (t == null) {throw new RuntimeException("Rules not part of a layer do not belong to a group.");}
		return (Consumes) t;
	}

	public static final class Update {
		public String ID; 
		public Tuple update;
		public Update(String ID, Tuple update) {
			this.ID = ID;
			this.update = update;
		}
	}
	
	public void apply(DisplayLayer<Glyph> table, Map<String, Tuple> sourceData) {
		java.util.List<Update> results = new ArrayList(table.getView().size());
		
		for (Glyph glyph: table.getView()) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			if (source == null) {continue;} 					//This dynamic updater does not apply to this glyph
			
			try {
				Tuple update = Interpreter.processSequential(source, getAction());
				if (update != null) {results.add(new Update(glyph.getID(), update));}
			}
			catch (Exception ex) {
				System.err.println("Error in dynamic update.");
				ex.printStackTrace();
			}			
		}
		
		((DoubleBufferLayer) table).updateAll(results);
	}	
		
	/**Should this dynamic rule be run now??*/
	public boolean requiresUpdate() {
		return ((StateQuery) getChild(1)).requiresUpdate();
	}
}
