package stencil.operator.util;

import stencil.tuple.Tuple;

public interface Invokeable<R> {
	public Object getTarget();

	
	/**Same as invoke, but gurantees that return value is wrapped as a Tuple.*/
	public abstract Tuple tupleInvoke(Object[] arguments) 
		throws MethodInvokeFailedException;

	/**Invoke some entity with the passed arguments.*/
	public abstract R invoke(Object[] arguments)
		throws MethodInvokeFailedException;

}