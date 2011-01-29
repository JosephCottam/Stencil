package stencil.interpreter.tree;

import stencil.interpreter.Viewpoint;
import stencil.module.operator.util.Invokeable;
import stencil.tuple.Tuple;

//TODO:Remove this class and just make a CallChain a list of invokeables and arg lists
public class Function implements Viewpoint<Function> {
	//TODO:Remvoe this enum when map is a higher-order function
	public static enum PASS_TYPES {DIRECT_YIELD, MAP, FOLD}
	
	private static final class FunctionApplicationException extends RuntimeException {
		public FunctionApplicationException(Function f, Tuple t, Exception e) {
			super(String.format("Error applying function %1$s with tuple %2$s.", f.name, t.toString()), e);
		}
	}

	private final String name;
	private final Invokeable inv;
	private final PASS_TYPES passType;
	private final Object[] args;
	
	public Function(String name, Invokeable inv, Object[] args, PASS_TYPES passType) {
		this.name = name;
		this.inv = inv;
		this.passType = passType;
		this.args = args;
	}
	
	public PASS_TYPES passType() {return passType;}
	
	/**
	 * Invokes the current function, and return the result.
	 *
	 * @param valueSource Tuple that supplies non-literal value arguments
	 * @return A tuple that is the result of the end of the call chain
	 * @throws Exception Exceptions may arise during method invocation (either reflection errors, or raised by the method inovked)
	 */
	public Tuple apply(Tuple valueSource) throws Exception {
		try {
			Object[] formals = TupleRef.resolveAll(args, valueSource);
			return inv.tupleInvoke(formals);//TODO:Support more rich return types
		} 
 		catch (Exception e) {throw new FunctionApplicationException(this, valueSource, e);} 		
	}	
	
	public Function viewpoint() {return new Function(name, inv.viewpoint(), args, passType);}
}
