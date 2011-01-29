package stencil.interpreter.tree;

import stencil.interpreter.tree.Predicate;
import stencil.parser.tree.util.Environment;

public class Consumes {
	private final int groupID;
	private final String stream;
	private final Predicate[] filters;
	private final Rule[] prefilter;
	private final Rule[] local;
	private final Rule[] results;
	private final Rule[] view;
	private final Rule[] canvas;
	private final DynamicRule[] dynamics;
	private final Object[] reducer;
	
	public Consumes(int groupID, String stream, Predicate[] filters, Rule[] prefilter, Rule[] local,
			Rule[] results, Rule[] view, Rule[] canvas, DynamicRule[] dynamics, Object[] reducer) {
		this.groupID = groupID;
		this.stream = stream;
		this.filters = filters;
		this.prefilter = prefilter;
		this.local = local;
		this.results = results;
		this.view = view;
		this.canvas = canvas;
		this.dynamics = dynamics;
		this.reducer = reducer;
	}
	
	public int groupID() {return groupID;}
	public String getStream() {return stream;}
	public Predicate[] getFiltersRules() {return filters;}
	public Rule[] getPrefilterRules() {return prefilter;}
	public Rule[] getLocalRules() {return local;}
	public Rule[] getResultRules() {return results;}
	public Rule[] getViewRules() {return view;}
	public Rule[] getCanvasRules() {return canvas;}
	public DynamicRule[] getDynamicRules() {return dynamics;}
	public Object[] getDynamicReducer() {return reducer;}

	/**Check that all filters match the passed environment.*/
	public boolean matches(Environment env) {return Predicate.matches(filters, env);}
}
