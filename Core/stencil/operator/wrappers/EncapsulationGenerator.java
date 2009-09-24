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
package stencil.operator.wrappers;

import static stencil.parser.ParserConstants.INIT_BLOCK_TAG;

import org.python.util.PythonInterpreter;

import stencil.operator.DynamicStencilOperator;
import stencil.operator.module.util.MutableModule;
import stencil.parser.tree.PythonFacet;
import stencil.parser.tree.Python;

import java.util.Map;
import java.util.HashMap;

/**Class to register Jython legends defined in a stencil definition.
 *
 * @author jcottam
 */
public class EncapsulationGenerator {
	private final Map<String, PythonInterpreter> environments = new HashMap<String, PythonInterpreter>();

	/**Generate encapsulations, only register if 'modules' is not null.
	 *
	 * @param program  Program to get Jython fragments from
	 * @param register Module
	 * @return Set of encapsulations.
	 */
	public DynamicStencilOperator generate(Python pythonSpec, MutableModule module) {
		assert module != null : "Null moduled passed.";
		
		registerEnv(pythonSpec.getEnvironment());
		JythonOperator legend = new JythonOperator(module.getModuleData().getName(), pythonSpec.getName());
		for (PythonFacet b: pythonSpec.getFacets()) {
			if (b.getName().equals(INIT_BLOCK_TAG)) {invokeInitBlock(b, pythonSpec.getEnvironment()); continue;}
			
			try {legend.add(new JythonEncapsulation(pythonSpec,b,this), b);}
			catch (Exception e) {throw new RuntimeException(String.format("Error creating encapsulation for facet %1$s (%2$s).", b.getName(), b.getBody()),e);}
		}

		module.addOperator(legend);
		
		return legend;
	}
	
	/**Invoke the init block of a spec in the named environment.*/ 
	private void invokeInitBlock(PythonFacet init, String env) {
		String body=init.getBody();
		PythonInterpreter environment = registerEnv(env);
		if (body !=null && !body.trim().equals("")) {
			environment.exec(body);
		}

	}
	
	/**Get the named environment.*/
	public PythonInterpreter getEnvironment(String name) {return environments.get(name);}

	/**Ensures that an environment with the given name exists.  It it already exists, it is
	 * returned.  If it does not exists, it is created and returned
	 * @param key Name o the environment
	 * @return The environment
	 */
	protected PythonInterpreter registerEnv(String key) {
		PythonInterpreter p;
		if (environments.containsKey(key)) {p= environments.get(key);}
		else {
			p = new PythonInterpreter();
			environments.put(key, p);
		}
		return p;
	}
}
