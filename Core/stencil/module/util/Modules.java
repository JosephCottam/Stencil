package stencil.module.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.interpreter.tree.Specializer;

/**A utility group for working with modules. Cannot be instantiated.*/
//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Modules {
	private Modules() {/*Utility class. Not instantiable.*/}

	/**Finds a member of a class as indicated by the "Target" attribute
	 * of the OperatorData object.
	 * 
	 * Class members are searched by name (not case sensitive).  It first
	 * looks at public static classes, then public static methods of the 
	 * source class. If a public static class is found, then that class
	 * is instantiated with the passed OperatorData object as the parameter.
	 * THEREFORE, any public static class to be instantiated through this
	 * mechanism must have a constructor that takes an OperatorData element
	 * as its only argument.  If a public static method is found, an Invokeable
	 * is created and a wrapping operator is returned.  The wrapping operator
	 * will use the given target for all methods of the operator.
	 * @throws SpecializationException 
	 **/
	public static StencilOperator instance(Class source, OperatorData operatorData, Object...args) {
		String module = operatorData.module();
		String name = operatorData.name();
		String target = operatorData.target();

		if (target == null) {throw new IllegalArgumentException("Cannot use null target.");}
		target = target.toUpperCase();
		
		for (Class c: source.getClasses()) {
			if (!Modifier.isStatic(c.getModifiers())) {continue;}
			if (!StencilOperator.class.isAssignableFrom(c)) {continue;}
			if (!Modifier.isPublic(c.getModifiers())) {continue;}
			if (!target.equals(c.getSimpleName().toUpperCase())) {continue;}

			//Try #1: All arguments
			try {
				Object[] fullArgs = new Object[args.length+1];
				System.arraycopy(args, 0, fullArgs, 1, args.length);
				fullArgs[0] = operatorData;
				return tryConstruct(c, fullArgs);
			}
			catch (InvocationTargetException ite) {
				if (ite.getCause() instanceof SpecializationException) {throw (SpecializationException) ite.getCause();}
				if (ite.getCause() instanceof RuntimeException) {throw (RuntimeException) ite.getCause();}
				else {throw new RuntimeException(ite);}
			}
			catch (NoSuchMethodException e) {
				//Try #2: Remove specializer (if provided)
				if (args.length >0 && args[0] instanceof Specializer) {
					try {
						Object[] fullArgs = new Object[args.length];
						System.arraycopy(args, 1, fullArgs, 1, args.length-1);
						fullArgs[0] = operatorData;
						return tryConstruct(c, fullArgs);
					} catch (Exception e2) {
						if (e2 instanceof RuntimeException) {throw (RuntimeException) e2;}
						else {throw new RuntimeException(e2);}
					}
				}
			}
		}
		
		
		
		for (Method m: source.getMethods()) {
			if (!Modifier.isPublic(m.getModifiers())) {continue;}
			if (!Modifier.isStatic(m.getModifiers())) {continue;}

			if (target.equals(m.getName().toUpperCase())) {
				Invokeable inv = new ReflectiveInvokeable(m);
				return new InvokeableOperator(operatorData, inv);
			}
		}
	
		throw new IllegalArgumentException(String.format("Operator %1$s not found in module %2$s.", name, module));
	}
	
	private static final StencilOperator tryConstruct(Class c, Object[] args) throws NoSuchMethodException, InvocationTargetException  {
		Class[] argTypes = new Class[args.length];
		for (int i=0; i< args.length; i++) {argTypes[i] = args[i].getClass();}
		Constructor constr = c.getConstructor(argTypes);
		try {
			return (StencilOperator) constr.newInstance(args);
		} 
		catch (InstantiationException e) {throw new RuntimeException(e);}
		catch (IllegalAccessException e) {throw new RuntimeException(e);}
	}
}
