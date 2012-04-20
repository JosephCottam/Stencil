package stencil.interpreter.tree;

import stencil.interpreter.Environment;
import stencil.interpreter.Viewpoint;
import stencil.tuple.Tuple;

public class OperatorRule implements Viewpoint<OperatorRule> {
	private final String parentName;
	private final Predicate[] preds;
	private final Rule rules;
	
	public OperatorRule(String parentName, Predicate[] preds, Rule rules) {
		this.parentName = parentName;
		this.preds = preds;
		this.rules = rules;
	}

	public Predicate[] getFilters() {return preds;}
	public Rule getRules() {return rules;}
	
	/**Rules 'match' when all of their predicates do.*/
	public boolean matches(Environment env) {return Predicate.matches(preds, env);}

	@Override
	public OperatorRule viewpoint() {
		final Predicate[] vpp = new Predicate[preds.length];
		for (int i=0; i<vpp.length; i++) {vpp[i] = preds[i].viewpoint();}

		return new OperatorRule(parentName, vpp, rules.viewpoint());
	}
	
	/**Apply the simple rule of this operator rule to the passed tuple.
	 * This is independent of 'matches', but should only be invoked
	 * if matches passes.
	 *
	 * @param source
	 * @return
	 */
	public Tuple invoke(Environment env) throws Exception {
		try {return rules.apply(env);}
		catch (Exception e) {
			throw new RuntimeException(String.format("Error invoking operator %1$s.", parentName),e);
		}
	}
}

