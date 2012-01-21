package stencil.interpreter.tree;

import stencil.interpreter.Environment;
import stencil.interpreter.NoOutput;
import stencil.interpreter.Viewpoint;
import stencil.module.operator.util.Invokeable;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;

public class CallChain implements Viewpoint<CallChain> {
	private final Invokeable[] invs;	//Friendly to allow direct access from rule/dynamic rule
	private final Object[][] args;
	private final Object[] pack; 		//TODO: Investigate freezing the pack as a function call
	
	public CallChain(Invokeable[] invs, Object[][] args, Object[] pack) {
		this.invs = invs;
		this.args = args;
		this.pack = pack;
	}
	
	public int depth() {return invs.length;}
	
	public CallChain viewpoint() {
		final Invokeable[] vp = new Invokeable[invs.length];
		for (int i=0; i< vp.length; i++) {
			vp[i] = invs[i].viewpoint();
		}
		return new CallChain(vp, args, pack);
	}
	
	
	/**Given the environment, apply a single step of this chain.
	 * Assumes the environment is appropriate for executing the requested step.
	 * 
	 * @param target  Which step to execute
	 * @param env The environment to execute in
	 * @return
	 */
	public Tuple applyStep(int target, Environment env) {
		final Invokeable inv = invs[target];
		Tuple result;
		try {
			Object[] formals = TupleRef.resolveAll(args[target], env);
			result = inv.tupleInvoke(formals);
		} 
		catch (NoOutput.Signal s) {result = NoOutput.TUPLE;}
		catch (Exception e) {
			throw new OperatorApplicationException(inv.targetIdentifier(), env, e);
		}
		return result;
	}
	
	public Tuple pack(Environment env) {
		try {
			Object[] values = TupleRef.resolveAll(pack, env);
			return new ArrayTuple(values);
		} catch (NoOutput.Signal s) {return NoOutput.TUPLE;}
	}
	
	private static final class OperatorApplicationException extends RuntimeException {
		public OperatorApplicationException(String name, Tuple t, Exception e) {
			super(String.format("Error applying function %1$s with environment %2$s.", name, t.toString()), e);
		}
	}
}
