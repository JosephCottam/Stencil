package stencil.module.operator.util;

import java.lang.reflect.*;
import static java.util.Arrays.deepToString;
import static java.lang.String.format;

import stencil.tuple.Tuple;
import stencil.types.Converter;


/**Combines a method and a target object (null for static items)
 * for later invoking.  Also includes the proper logic for performing the
 * invocation from an array of values.*/

//final because it is immutable
public final class ReflectiveInvokeable<T, R> implements Invokeable<R> {
	/**Method to be invoked.*/
	private final Method method;
	
	/**Object to invoke method on, null for static methods.*/
	private final T target;

	//Cache objects common allocated/referenced while invoking but never materially changing
	private final Class[] paramTypes;
	private final Object[] args;
	
	public ReflectiveInvokeable(Method method) {this(method, null);}
	public ReflectiveInvokeable(String method, Class target) {this(findMethod(method, target), null);}
	public ReflectiveInvokeable(String method, T target) {this(findMethod(method, target.getClass()), target);}
	public ReflectiveInvokeable(Method method, T target) {
		if (Modifier.isStatic(method.getModifiers()) && target != null) {
			throw new IllegalArgumentException("Cannot supply a target for static methods.");
		}

		if (target != null && !method.getDeclaringClass().isAssignableFrom(target.getClass())) {
			throw new IllegalArgumentException("Method and target object are not type compatible.");
		}
		
		this.method = method;
		this.target =target;
		paramTypes = method.getParameterTypes();
		args = new Object[paramTypes.length];
	}

	//Find the method amidst the class
	private static Method findMethod(String methodName, Class clss) {
		for (Method m: clss.getMethods()) {
			if (m.getName().equals(methodName)) {return m;}
		}
		
		throw new IllegalArgumentException(String.format("Could not find method named %1$s in class %2$s.", methodName, clss.getName()));
	}
	
	/**Is the underlying method static?*/
	public boolean isStatic() {return target==null;}
	public T getTarget() {return target;}
	public Method getMethod() {return method;}
	
	
	public Tuple tupleInvoke(Object[] arguments) throws MethodInvokeFailedException {
		R result = invoke(arguments);
		Tuple t=null;
		if (result != null) {t=Converter.toTuple(result);}
		return t;
	}
	
	public R invoke(Object[] arguments) throws MethodInvokeFailedException {
		int expectedNumArgs = paramTypes.length;
		boolean isVarArgs =method.isVarArgs();
		R result;

		try {
			if (isVarArgs) {
				if (!(arguments.length >= expectedNumArgs-1)) {
					throw new MethodInvokeFailedException(String.format("Incorrect number of arguments for method specified invoking varArgs method %1$s (expected at least %2$s; received: %3$s).", method.getName(), expectedNumArgs-1, arguments.length));
				}

				//Copy over fixed arguments
				validateTypes(arguments, paramTypes, 0, args.length-1, args);

				//Prepare variable argument for last position of arguments array
				
				
				Object varArgs;
				if (arguments.length > 0 && arguments[0] != null && arguments[0].getClass().isArray()) {
					Class type = paramTypes[paramTypes.length-1];
					 //Interesting case:  arguments contains a pre-packed var-args array
					if (arguments[0].getClass().isAssignableFrom(type)) {
						varArgs = type.cast(arguments[0]);
					} else {
						varArgs = validateType(arguments[0], type.getComponentType());
					}
					
				} else {
					Class type = paramTypes[paramTypes.length-1].getComponentType();
					Object[] remainingArguments = new Object[arguments.length-expectedNumArgs+1];
					System.arraycopy(arguments, expectedNumArgs-1, remainingArguments, 0, remainingArguments.length);
					varArgs = validateType(remainingArguments, type);
				}
				args[args.length-1] = varArgs;
			} else {
				if (arguments.length != expectedNumArgs) {
					throw new MethodInvokeFailedException(format("Incorrect number of arguments for method specified (expected %1$s; received: %2$s).", expectedNumArgs, arguments.length), method, target);
				}

				validateTypes(arguments, paramTypes, 0, arguments.length, args);
			}
		} catch (Exception e) {
			throw new MethodInvokeFailedException(format("Exception thrown peparing arguments: %1$s.", deepToString(arguments)),method, target, e);
		}
			
		try {
			result = (R) method.invoke(target, args);
		} catch (Exception e) {
		 	throw new MethodInvokeFailedException(String.format("Exception with arguments %1$s.", deepToString(args)),method, target, e);
		}
		return result;
	}
	
	/**Copies values from the arguments array to the result array, converting them per the type along the way.
	 * It is assumed that arguments[i] will be converted to type[i] and put in results[i]
	 * 
	 * @param arguments  List of values to put into the results
	 * @param types Types to convert to along the way. 
	 * @param start Index to start converting at
	 * @param end Index to end converting at
	 * @param result Place to store results.
	 */
	private void validateTypes(Object[] arguments, Class<?>[] types, int start, int end, Object result) {
		for (int i=start; i< end; i++) {
			Array.set(result, i, Converter.convert(arguments[i], types[i]));
		}
	}

	/**Copes values from an arguments array to the result array, converting them per the type along the way.
	 * Each argument in arguments will be converted to the type given; the return result will
	 * have be an array of the type passed as well.
	 * 
	 * @param arguments  An array object of the arguments to convert
	 * @param type The type to convert into
	 */
	private Object validateType(Object arguments, Class type) {
		Object varArgs = Array.newInstance(type, Array.getLength(arguments));
		for (int i=0; i< Array.getLength(varArgs); i++) {
			Array.set(varArgs, i, Converter.convert(Array.get(arguments, i), type));
		}
		return varArgs;
	}
	
	
	public int hashCode() {return method.hashCode();}
	
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof ReflectiveInvokeable)) {return false;}
		
		ReflectiveInvokeable o = (ReflectiveInvokeable) other;
		boolean result = target.equals(o.target) && method.equals(o.method) && o.args.length == this.args.length; 
		return result;
	}
	

}
