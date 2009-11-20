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
package stencil.operator.module.util;

import static stencil.parser.ParserConstants.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import stencil.operator.StencilOperator;
import stencil.operator.module.OperatorData.OpType;
import stencil.operator.wrappers.InvokeableLegend;
import stencil.tuple.Tuple;

/**A utility group for working with modules. Cannot be instantiated.*/
//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Modules {
	public static final String MODULE_FIELD_NAME = "MODULE_FIELD";

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
		else {prefix = prefix + NAME_SEPARATOR;}
		return prefix + name;
	}

	/**Remove the prefix from a name (if any exists).*/
	public static String removePrefix(String name) {
		String[] parts = name.split(NAME_SEPARATOR);
		return parts.length ==0 ? name : parts[parts.length-1];
	}

	/**Wraps a public static method as a legend or returns a public static class instance;
	 * performs no specialization.
	 * 	
	 * This method will only return instances to public, static methods 
	 * of a class that return a Tuple OR inner classes.  
	 * It does so without regard to the method signature, so the
	 * first method of a given name encountered is the one returned (sorry, overloading
	 * is no supported in Stencil).
	 * 
	 * WARNING: The comparisons are not case-sensitive.
	 * 
	 * @param source The class which provides the method being wrapped
	 * @param module The module that contains that supposed contains the operator
	 * @param name The name of the target (method or class)
	 * */
	public static StencilOperator instance(Class source, String target, String module, String name) {
		if (target == null) {throw new IllegalArgumentException("Cannot use null target.");}
		target = target.toUpperCase();
		
		for (Class c: source.getClasses()) {
			if (!Modifier.isStatic(c.getModifiers())) {continue;}
			if (!StencilOperator.class.isAssignableFrom(c)) {continue;}
			if (!Modifier.isPublic(c.getModifiers())) {continue;} //Might be a superfluous test

			try {if (target.equals(c.getSimpleName().toUpperCase())) {return (StencilOperator) c.getConstructor().newInstance();}}
			catch (Exception e) {/*Ignore, must not be the right thing if it can't be instantiated!*/}
		}
		
		for (Method m: source.getMethods()) {
			if (!Modifier.isPublic(m.getModifiers())) {continue;}
			if (!Modifier.isStatic(m.getModifiers())) {continue;}
			if (!Tuple.class.isAssignableFrom(m.getReturnType())) {continue;}

			if (target.equals(m.getName().toUpperCase())) {return new InvokeableLegend(name, m);}
		}
	

		throw new IllegalArgumentException(String.format("Operator %1$s not found in module %2$s.", name, module));
		
	}

	/**Verify that the passed method could be used as stencil legend.*/
	public static boolean isFacet(Method m) {
		return stencil.tuple.Tuple.class.isAssignableFrom(m.getReturnType());
	}
	
	/**Return a mutableLegendData object with the default facets and specializer.
	 * 
	 * Default type for facets is Categorize.
	 * Default return prototype is singleton VALUE.
	 * Default facet set is Map and Query.
	 * 
	 **/
	public static MutableOperatorData basicLegendData(String module, String name) {
		return basicLegendData(module, name, OpType.PROJECT, "VALUE");
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
	public static MutableOperatorData basicLegendData(String module, String name, OpType type, String...fields) {
		MutableOperatorData legendData = new MutableOperatorData(module, name, SIMPLE_SPECIALIZER);
		legendData.addFacet(new BasicFacetData("Map", type, fields));
		legendData.addFacet(new BasicFacetData("Query", type, fields));
		return legendData;
	}
}
