package stencil.module.operator.util;

import stencil.interpreter.Viewpoint;
import stencil.tuple.Tuple;

public interface Invokeable<R> extends Viewpoint<Invokeable> {
	/**Same as invoke, but the returned value will be a tuple.
	 * If the returned value would be wrapped as an array tuple, 
	 * the passed 'container' tuple will be used.  The container
	 * value MUST NOT be null.
     **/
	public Tuple tupleInvoke(Object[] arguments) 
		throws MethodInvokeFailedException;

	/**Invoke some entity with the passed arguments.*/
	public R invoke(Object[] arguments)
		throws MethodInvokeFailedException;
	
	/**How should the target be identified when error messages are produced?**/
	public String targetIdentifier();
	
	@Override
	public Invokeable viewpoint();	
}