package stencil.interpreter.tree;

import stencil.interpreter.Environment;
import stencil.interpreter.NoOutput;
import stencil.interpreter.Viewpoint;
import stencil.module.operator.util.Invokeable;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple; 

public class CallChain implements Viewpoint<CallChain> {
	private final Invokeable[] invs;
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
	
	/**Execute the call chain, all the way through the pack.
	 * 	 
	 * Short-circuiting occurs when the method invoked returns null.  If this is the case,
	 * a null is immediately returned from the function chain.  This means that no further
	 * actions will be taken in the chain.  If a 'null' is a valid return value from a given
	 * function, then you must wrap it in a tuple and give it an appropriate key.
	 *
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public Tuple apply(Environment env) throws Exception {		
		for (int target=0; target< invs.length; target++) {
			final Invokeable inv = invs[target];
			Tuple result;
			try {
				Object[] formals = TupleRef.resolveAll(args[target], env);
				result = inv.tupleInvoke(formals);
			} 
			catch (NoOutput.Signal s) {result = NoOutput.TUPLE;}
			catch (Exception e) {
				throw new FunctionApplicationException(inv.targetIdentifier(), env, e);
			}
			env.extend(result);
		}

		try {
			Object[] values = TupleRef.resolveAll(pack, env);
			return new ArrayTuple(values);
		} catch (NoOutput.Signal s) {return NoOutput.TUPLE;}
	}
	
	private static final class FunctionApplicationException extends RuntimeException {
		public FunctionApplicationException(String name, Tuple t, Exception e) {
			super(String.format("Error applying function %1$s with environment %2$s.", name, t.toString()), e);
		}
	}
}
