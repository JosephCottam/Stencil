package stencil.operator.util;

public interface Invokeable<R> {
	public Object getTarget();
	
	public abstract R invoke(Object[] arguments)
			throws MethodInvokeFailedException;

}