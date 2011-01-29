package stencil.interpreter.tree;

import stencil.interpreter.RuleAbortException;
import stencil.interpreter.Viewpoint;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public class Rule implements Viewpoint<Rule> {
	private final String path;
	private final CallChain chain;
	private final Target target;
	
	public Rule(String path, CallChain chain, Target target) {
		this.path = path;
		this.chain = chain;
		this.target = target;
	}

	public String path() {return path;}
	public TuplePrototype prototype() {return target.prototype();}
	
	
	/**Execute the action and return a tuple with fields
	 * corresponding to the prototype and values from the action.
	 *
	 * @param source
	 * @return
	 */
	public Tuple apply(Environment env) throws Exception {
		Environment ruleEnv = env.ensureCapacity(env.size() + chain.depth());
		Tuple t = chain.apply(ruleEnv);
		if (t == null) {throw new RuleAbortException(this);}
		return target.finalize(t);
	}
	
	public Rule viewpoint() {return new Rule(path, chain.viewpoint(), target);}	
}
