/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.module.util;

import static stencil.parser.ParserConstants.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.module.util.FacetData.MemoryUse;
import stencil.parser.ParserConstants;
import stencil.parser.tree.Specializer;

/**A utility group for working with modules. Cannot be instantiated.*/
//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Modules {
	private Modules() {/*Utility class. Not instantiable.*/}
	
	/**Convert a method name to the standard case structure used by Stencil from the Java standard. */
	public static final String stencilCase(String name) {return name.substring(0,1).toUpperCase() + name.substring(1);}

	/**Convert a method name to the standard case structure used by Stencil from the Java standard. */
	public static final String javaCase(String name) {return name.substring(0,1).toLowerCase() + name.substring(1);}


	/**Given an array of names, append a prefix to them.
	 *
	 * @param names
	 * @param prefix
	 * @return
	 */
	public static String[] prefixNames(String prefix, String... names) {
		String[] longNames = new String[names.length];

		for (int i =0; i< names.length; i++) {
			longNames[i] = prefixName(prefix, names[i]);
		}
		return longNames;
	}

	public static String prefixName(String prefix, String name) {
		if (prefix == null || prefix.trim().equals("")) {prefix = "";}
		else {prefix = prefix + NAME_SPACE;}
		return prefix + name;
	}

	/**Remove the prefix from a name (if any exists).*/
	public static String removePrefix(String name) {
		String[] parts = name.split(NAME_SEPARATOR);
		return parts.length ==0 ? name : parts[parts.length-1];
	}

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
	 **/
	public static StencilOperator instance(Class source, OperatorData operatorData, Object...args) {
		String module = operatorData.getModule();
		String name = operatorData.getName();
		String target = operatorData.getTarget();

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
			catch (Exception e) {
				//Try #2: Remove specializer (if provided)
				if (args.length >0 && args[0] instanceof Specializer) {
					try {
						Object[] fullArgs = new Object[args.length];
						System.arraycopy(args, 1, fullArgs, 1, args.length-1);
						fullArgs[0] = operatorData;
						return tryConstruct(c, fullArgs);
					} catch (Exception e2) {/*Ignore, must not be the right thing if it can't be instantiated!*/}
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
	
	private static final StencilOperator tryConstruct(Class c, Object[] args) throws Exception {
		Class[] argTypes = new Class[args.length];
		for (int i=0; i< args.length; i++) {argTypes[i] = args[i].getClass();}
		Constructor constr = c.getConstructor(argTypes);
		return (StencilOperator) constr.newInstance(args);
	}
	
	/**Return a meta-data object with the default facets and specializer.
	 * 
	 * Default type for facets is Categorize.
	 * Default return prototype is singleton VALUE.
	 * Default facet set is Map and Query.
	 * 
	 **/
	public static OperatorData basicOperatorData(String module, String name) {
		return basicOperatorData(module, name, "VALUE");
	}
	
	/**Produce a mutable operator meta-data object with the names, op-type and operator fields
	 * specified.  Will use the default face set.
	 * 
	 * @param module
	 * @param name
	 * @param type
	 * @param fields
	 * @return
	 */
	public static OperatorData basicOperatorData(String module, String name, String...fields) {
		OperatorData od = new OperatorData(module, name, BASIC_SPECIALIZER, null);
		od.addFacet(new FacetData(ParserConstants.MAP_FACET, MemoryUse.WRITER, fields));
		od.addFacet(new FacetData(ParserConstants.QUERY_FACET, MemoryUse.READER, fields));
		return od;
	}
}
