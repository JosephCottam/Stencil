package stencil.operator.util;

import java.lang.reflect.*;

import stencil.tuple.Tuple;
import stencil.types.Converter;


/**Combines a method and a target object (null for static items)
 * for later invoking.  Also includes the proper logic for performing the
 * invocation from an array of values.*/

//final because it is immutable
public final class Invokeable<T, R> {
	/**Method to be invoked.*/
	private Method method;
	/**Object to invoke method on, null for static methods.*/
	private T target;
	
	public Invokeable(Method method) {this(method, null);}
	public Invokeable(Method method, T target) {initialize(method, target);}
	public Invokeable(String method, Class target) throws NoSuchMethodException {initialize(getMethod(method, target), null);}
	public Invokeable(String method, T target) throws NoSuchMethodException {initialize(getMethod(method, target.getClass()), target);}

	
	private void initialize(Method method, T target) {
		if (Modifier.isStatic(method.getModifiers()) && target != null) {
			throw new IllegalArgumentException("Cannot supply a target for static methods.");
		}

		if (target != null && !method.getDeclaringClass().isAssignableFrom(target.getClass())) {
			throw new IllegalArgumentException("Method and target object are not type compatible.");
		}

		if (!method.getReturnType().isAssignableFrom(Tuple.class)) {
			throw new IllegalArgumentException("Can only wrap methods with return type Tuple.");
		}
		
		this.method = method;
		this.target =target;
	}

	//Find the method amidst the class
	private Method getMethod(String methodName, Class clss) throws NoSuchMethodException {
		for (Method m: clss.getMethods()) {
			if (m.getName().equals(methodName)) {return m;}
		}
		throw new NoSuchMethodException(String.format("Could not find method named %1$s in class %2$s.", methodName, clss.getName()));
	}
	
	/**Is the underlying method static?*/
	public boolean isStatic() {return target==null;}
	public T getTarget() {return target;}
	
	
	public R invoke(Object[] arguments) throws MethodInvokeFailedException {
		int expectedNumArgs = method.getParameterTypes().length;
		R result;

		if ((arguments.length != expectedNumArgs && !method.isVarArgs()) ||
			(arguments.length < expectedNumArgs &&
					(arguments.length == expectedNumArgs -1 && !method.isVarArgs()))) {
			throw new MethodInvokeFailedException(String.format("Incorrect number of arguments for method specified invoking %1$s (expected %2$s; received: %3$s).", method.getName(), expectedNumArgs, arguments.length));
		}

		Object[] args = null;
		try {
			if (method.isVarArgs()) {
				args = new Object[method.getParameterTypes().length];

				//Copy over fixed arguments
				for (int i=0; i< args.length-1; i++) {args[i] = arguments[i];}

				//Prepare variable argument for last position of arguments array
				Class type = method.getParameterTypes()[method.getParameterTypes().length-1].getComponentType();
				Object[] varArgs = (Object[]) java.lang.reflect.Array.newInstance(type, (arguments.length-expectedNumArgs)+1);
				System.arraycopy(arguments, args.length-1, varArgs, 0, varArgs.length);
				args[args.length-1] = varArgs;
				result = (R) method.invoke(target, args);
			} else {
//				args = validateTypes(arguments, method.getParameterTypes());
				args = arguments;
				result = (R) method.invoke(target, args);
			}
		} catch (Exception e) {
		 	throw new MethodInvokeFailedException(String.format("Exception thrown invoking %1$s with arguments %2$s.", method.getName(), java.util.Arrays.deepToString(args)), e);
		}
		return result;
	}
	
	private Object[] validateTypes(Object[] arguments, Class<?>[] types) {
		Object[] results = new Object[arguments.length];
		
		for (int i=0; i< arguments.length; i++) {
			if (arguments.getClass().isAssignableFrom(types[i])) {
				results[i] = arguments[i];
			} else {
				results[i] = Converter.convert(arguments[i], types[i]);
			}
		}
		return results;
	}
	
}
