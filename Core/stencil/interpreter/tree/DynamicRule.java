package stencil.interpreter.tree;

import java.util.ArrayList;
import java.util.Map;

import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.interpreter.Interpreter;
import stencil.interpreter.Viewpoint;
import stencil.tuple.Tuple;
import stencil.tuple.TupleAppender;
import stencil.tuple.instances.PrototypedTuple;
import java.util.List;

public class DynamicRule implements Viewpoint<DynamicRule> {
	private final int groupID;	//TODO: Convert to a string (for better error reporting)
	private final String layerName;
	private final Rule rule;
	private final StateQuery query;
	
	public DynamicRule (String layerName, int groupID, Rule rule, StateQuery query) {
		this.layerName = layerName;
		this.groupID = groupID;
		this.rule = rule;
		this.query = query;
	}
	
	public String layerName() {return layerName;}
	public int gropuID() {return groupID;}
	public Rule getAction() {return rule;}
	public StateQuery getStateQuery() {return query;}
	
	
	public List<Tuple> apply(DisplayLayer<Glyph> table) {
		LayerView<Glyph> view = table.getView();
		
		java.util.List<Tuple> results = new ArrayList(view.size());
		Map<String, LayerView.DynamicEntry> sourceData = view.getSourceData();
		
		for (Glyph glyph: view) {
			LayerView.DynamicEntry entry = sourceData.get(glyph.getID());		//Get associated source data
			if (entry.groupID != groupID) {continue;}
			Tuple source = entry.t;
			
			try {
				Tuple result = Interpreter.processTuple(source, rule);
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
	
	public DynamicRule viewpoint() {
		Rule ruleVP = this.rule.viewpoint();
		StateQuery queryVP= this.query.viewpoint();
		return new DynamicRule(layerName, groupID, ruleVP, queryVP);
	}
}
