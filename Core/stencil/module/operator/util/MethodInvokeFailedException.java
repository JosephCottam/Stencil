package stencil.module.operator.util;

import java.lang.reflect.Method;

public class MethodInvokeFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MethodInvokeFailedException() {super();}
	public MethodInvokeFailedException(String message, Throwable arg1) {super(message, arg1);}
	public MethodInvokeFailedException(String message, Method m, Object t, Throwable arg1) {super(formatMessage(message, m, t), arg1);}
	public MethodInvokeFailedException(String message, Method m, Object t) {super(formatMessage(message, m, t));}
	public MethodInvokeFailedException(String message) {super(message);}
	public MethodInvokeFailedException(Throwable message) {super(message);}
	
	private static String formatMessage(String message, Method method, Object target) {
		String targetName;
		if (target == null) {
			targetName = method.getDeclaringClass().getSimpleName();
		} else {
			targetName = target.getClass().getSimpleName();
		}
		return String.format("%1$s.%2$s: %3$s", targetName, method.getName(), message);
	}


}
