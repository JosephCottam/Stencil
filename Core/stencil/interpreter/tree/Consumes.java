package stencil.interpreter.tree;

import stencil.interpreter.Environment;
import stencil.interpreter.tree.Predicate;

public class Consumes {
	private final int groupID;
	private final String stream;
	private final Predicate[] filters;
	private final Rule prefilter;
	private final Rule local;
	private final Rule results;
	private final DynamicRule[] dynamics;
	
	public Consumes(int groupID, String stream, Predicate[] filters, Rule prefilter, Rule local,
			Rule results, DynamicRule[] dynamics) {
		this.groupID = groupID;
		this.stream = stream;
		this.filters = filters;
		this.prefilter = prefilter;
		this.local = local;
		this.results = results;
		this.dynamics = dynamics;
	}
	
	public int groupID() {return groupID;}
	public String getStream() {return stream;}
	public Predicate[] getFiltersRules() {return filters;}
	public Rule getPrefilterRule() {return prefilter;}
	public Rule getLocalRule() {return local;}
	public Rule getResultRule() {return results;}
	public DynamicRule[] getDynamicRules() {return dynamics;}

	/**Check that all filters match the passed environment.*/
	public boolean matches(Environment env) {return Predicate.matches(filters, env);}
}
