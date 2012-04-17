package stencil.interpreter.tree;

import stencil.interpreter.Viewpoint;
import stencil.tuple.prototype.TuplePrototype;

public class OperatorFacet implements Viewpoint<OperatorFacet> {
	private final TuplePrototype arguments, results;
	private final OperatorRule[] rules;
	private final Rule prefilters;
	private final String name;
	
	public OperatorFacet(String name, TuplePrototype arguments, TuplePrototype results, Rule prefilters, OperatorRule[] rules) {
		this.name = name;
		this.arguments = arguments;
		this.results = results;
		this.prefilters = prefilters;
		this.rules = rules;
	}

	public String getName() {return name;}
	
	public TuplePrototype getArguments() {return arguments;}
	public stencil.tuple.prototype.TuplePrototype getResults() {return results;}	
	
	
	public OperatorRule[] getRules() {return rules;}
	public Rule getPrefilterRules() {return prefilters;}
	
	@Override
	public OperatorFacet viewpoint() {
		final OperatorRule[] vpr = new OperatorRule[rules.length];
		for (int i=0; i<vpr.length; i++) {vpr[i] = rules[i].viewpoint();}
				
		return new OperatorFacet(name, arguments, results, prefilters.viewpoint(), vpr);
	}
}
