package stencil.interpreter.tree;

import stencil.interpreter.Viewpoint;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.TupleAppender;

public class OperatorRule implements Viewpoint<OperatorRule> {
	private final String parentName;
	private final Predicate[] preds;
	private final Rule[] rules;
	
	public OperatorRule(String parentName, Predicate[] preds, Rule[] rules) {
		this.parentName = parentName;
		this.preds = preds;
		this.rules = rules;
	}

	public Predicate[] getFilters() {return preds;}
	public Rule[] getRules() {return rules;}
	
	/**Rules 'match' when all of their predicates do.*/
	public boolean matches(Environment env) {return Predicate.matches(preds, env);}

	public OperatorRule viewpoint() {
		final Predicate[] vpp = new Predicate[preds.length];
		for (int i=0; i<vpp.length; i++) {vpp[i] = preds[i].viewpoint();}

		final Rule[] vpr = new Rule[rules.length];
		for (int i=0; i<vpr.length; i++) {vpr[i] = rules[i].viewpoint();}

		return new OperatorRule(parentName, vpp, vpr);
	}
	
	/**Apply the simple rule of this operator rule to the passed tuple.
	 * This is independent of 'matches', but should only be invoked
	 * if matches passes.
	 *
	 * @param source
	 * @return
	 */
	public Tuple invoke(Environment env) throws Exception {
		int ruleCount = 0;
		Tuple result = null;
		for (Rule rule: getRules()) {
			Tuple buffer;
			
			try {buffer = rule.apply(env);}
			catch (Exception e) {
				throw new RuntimeException(String.format("Error invoking sub rule %1$d on operator %2$s.", ruleCount, parentName),e);
			}

			if (buffer == null) {result = null; break;}
			result= TupleAppender.append(buffer,result);
			ruleCount++;
		}

		return result;
	}
}

