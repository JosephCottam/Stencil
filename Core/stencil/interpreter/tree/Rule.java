package stencil.interpreter.tree;

import stencil.interpreter.Environment;
import stencil.interpreter.RuleAbortException;
import stencil.interpreter.Viewpoint;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import static stencil.tuple.prototype.TuplePrototypes.EMPTY_PROTOTYPE;
import static stencil.tuple.Tuples.EMPTY_TUPLE;

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
	 * Execution occurs data-flow style.
	 *
	 * @param source
	 * @return
	 */
	public Tuple apply(Environment env) throws Exception {
		env = env.ensureCapacity(env.size() + chain.depth());
		final int depth = chain.depth();
		for (int target=0; target< depth; target++) {
			Tuple result = chain.applyStep(target, env);
			env.extend(result);
		}
		Tuple t= chain.pack(env);

		if (t == null) {throw new RuleAbortException(this);}
		return target.finalize(t);
	}
	
	public Rule viewpoint() {return new Rule(path, chain.viewpoint(), target);}	
	
	public static final Rule EMPTY_RULE = new Rule(null, null, null)  {
		public String path() {return "No path; empty rule.";}
		public TuplePrototype prototype() {return EMPTY_PROTOTYPE;}
		public Tuple apply(Environment env) {return EMPTY_TUPLE;}
		public Rule viewpoint() {return this;}
	};
}
