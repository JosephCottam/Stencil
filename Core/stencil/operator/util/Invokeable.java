package stencil.operator.util;

import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;

public interface Invokeable<R> {
	public Object getTarget();

	
	/**Same as invoke, but the returned value will be a tuple.
	 * If the returned value would be wrapped as an array tuple, 
	 * the passed 'container' tuple will be used.  The container
	 * value MUST NOT be null.
     **/
	public abstract Tuple tupleInvoke(Object[] arguments, ArrayTuple container) 
		throws MethodInvokeFailedException;

	/**Invoke some entity with the passed arguments.*/
	public abstract R invoke(Object[] arguments)
		throws MethodInvokeFailedException;

}