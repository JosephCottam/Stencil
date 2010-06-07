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
package stencil.module.operator.wrappers;

import org.python.util.PythonInterpreter;
import org.python.core.*;

import stencil.parser.tree.Python;
import stencil.parser.tree.PythonFacet;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

import java.util.Arrays;
import java.lang.reflect.Method;

/**Container of Jython code that has been defined in a stencil definition.
 * This container is suitable for use with the stencil operator method invocation routines 
 * and performs the translation into and out of the jython environment for values passed to and
 * returned from the jython environment.
 *
 * Jython bodies that are named after the ParserConstants.INIT_BLOCK_TAG are
 * automatically executed on construction of the encapsulation.
 *
 * @author jcottam
 *
 */
public final class JythonEncapsulation {
	public static final String INVOKE_METHOD = "invoke";
	public static final Object ABORT_VALUE = "STENCIL:CANCEL";

	protected final PythonFacet facet;
	protected final PythonInterpreter environment;
	protected final PyCode codeCache;

	public JythonEncapsulation(Python python, PythonFacet facet, EncapsulationGenerator envSource) {
		assert !facet.isInit() : "Should never create an encapusluation for init block.";

		this.facet = facet;
		environment = envSource.registerEnv(python.getEnvironment());
		codeCache = Py.compile(new java.io.ByteArrayInputStream(facet.getBody().getBytes()), null, CompileMode.exec);
	}
	
	public Tuple invoke(Object... args) {
		if (args.length != getArguments().size()) {throw new IllegalArgumentException(String.format("Must call with argument count equal to expected count.  Received %1$d but expected %2$d.", args.length, getArguments().size()));}
		assert environment != null;
		
		synchronized(environment) {
		
			//Stock the Python interpreter with the appropriate values
			for (int i=0; i< args.length; i++) {
				String name = getArguments().get(i).getFieldName();
				environment.set(name, args[i]);
			}
	
			//Invoke the pre-compiled body
			environment.exec(codeCache);
	
			TuplePrototype returns = getReturns();
			int returnCount = returns.size();
			Object[] vals = new Object[returnCount];
			for (int i=0; i< returnCount; i++) {
				String name = returns.get(i).getFieldName();
				vals[i] = pyToJava(environment.get(name));
			}
			return new PrototypedTuple(returns, Arrays.asList(vals));
		}
	}


	/**Attempts to converts a Python Object to a more specific java object by calling the __tojava__ method.
	 * Mappings included are as follows:
	 *   null      --> null
	 *   PyInteger --> Integer
	 *   PyFloat   --> Double
	 *   PyString  --> String
	 *
	 *   This can also unwrap java Colors, Shapes, Lists, etc if they PyObject implements the appropriate interfaces.
	 *
	 * @param tree Python object to convert
	 * @return Most specific corresponding java object
	 */
	public Object pyToJava(PyObject source) {
		if (source == null) {return null;}

		return source.__tojava__(java.lang.Object.class);
	}

	public String getName() {return facet.getName();}
	public TuplePrototype getArguments() {return facet.getArguments();}
	public TuplePrototype getReturns() {return facet.getResults();}
	public String getAnnotation(String name) {return facet.getAnnotations().get(name).getText();}
	
	
	public Method getInvokeMethod() {
		try {return getClass().getMethod(INVOKE_METHOD, Object[].class);}
		catch (Exception e) {throw new Error("Error finding method invoke method in encapsulation.");}
	}
}
