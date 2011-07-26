package stencil.interpreter.tree;

import java.util.ArrayList;
import java.util.Map;

import stencil.display.DynamicBindSource;
import stencil.display.Glyph;
import stencil.interpreter.Interpreter;
import stencil.interpreter.UpdateableComposite;
import stencil.tuple.Tuple;
import java.util.List;

public class DynamicRule implements UpdateableComposite<DynamicRule> {
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
	public int groupID() {return groupID;}
	public Rule action() {return rule;}
	public StateQuery stateQuery() {return query;}
	
	
	public List<Tuple> apply(DynamicBindSource<? extends Glyph> view) {
		java.util.List<Tuple> results = new ArrayList(view.size());
		Map<String, Tuple> sourceData = view.getSourceData();
		
		for (Glyph glyph: view) {
			Tuple source = sourceData.get(glyph.getID());		//Get associated source data
			assert source != null : "Null dynamic binding source discovered.";

			try {
				Tuple result = Interpreter.processTuple(source, rule);
				results.add(result);
			} catch (Exception ex) {
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
