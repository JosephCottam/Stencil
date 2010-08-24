package stencil.parser.tree;


import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.interpreter.Interpreter;
import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;
import stencil.tuple.TupleAppender;
import stencil.tuple.instances.PrototypedTuple;

public class DynamicRule extends StencilTree {
	public DynamicRule(Token token) {super(token);}

	public Rule getAction() {return (Rule) getChild(0);}
	
	/**What group does this rule belong to?*/
	public Consumes getGroup() {
		Tree t = this.getAncestor(StencilParser.CONSUMES);
		if (t == null) {throw new RuntimeException("Rules not part of a consumes block do not belong to a group.");}
		return (Consumes) t;
	}

	public java.util.List<Tuple> apply(DisplayLayer<Glyph> table) {
		LayerView<Glyph> view = table.getView();
		
		java.util.List<Tuple> results = new ArrayList(view.size());
		Map<String, LayerView.DynamicEntry> sourceData = view.getSourceData();
		int groupID = getGroup().groupID();
		
		for (Glyph glyph: view) {
			LayerView.DynamicEntry entry = sourceData.get(glyph.getID());		//Get associated source data
			if (entry.groupID != groupID) {continue;}
			Tuple source = entry.t;
			
			try {
				Tuple result = Interpreter.processSequential(source, getAction());
				if (result != null) {
					Tuple id = PrototypedTuple.singleton("ID", glyph.getID());
					Tuple update = TupleAppender.append(id, result);
					results.add(update);
				}
			}
			catch (Exception ex) {
				System.err.println("Error in dynamic update.");
				ex.printStackTrace();
			}			
		}
		return results;
	}	
		
	public StateQuery getStateQuery() {return ((StateQuery) getChild(1));}
}