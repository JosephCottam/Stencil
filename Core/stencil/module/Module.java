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
package stencil.module;

import stencil.module.operator.StencilOperator;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;

/**A module is a collection of related operators.
 * The module interface describes the elements required
 * for a collection to managed.  The implementing class
 * is responsible for coordinating the package members
 * with the interpreter/compiler.  Packages need not contain
 * all of the methods/instructions for their exported methods,
 * but they are responsible for proper initialization
 * (any initialization not performed by the package root
 * will have to be explicitly included in the Stencil).
 *
 * In addition to the method specified, a module must have
 * a zero-argument constructor if it is to be loaded into the 
 * ModuleCache through the startup mechanisms.
 */
public interface Module {
	/**Get an instance of the operator of the given name, instantiated
	 * with the given specializer.  Name may should NOT have a prefix.
	 * 
	 * @throws IllegalArgumentException Name passed is not known by this module.
	 * @throws SpecializationException Specializer passed not permitted with the given operator.
	 * @return
	 */
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException, IllegalArgumentException;
	
	/**Get an instance of a higher-oder operator (OPTIONAL OPERATION).
	 * Higher-order operators take operators as arguments, and thus require the modules cache.
	 * If the operator is tagged with the OperatorData.HIGHER_ORDER_TAG field, the instantiation will occur through this method.
	 * 
	 * @param name
	 * @param context
	 * @param specializer
	 * @param modules
	 * @return
	 * @throws SpecializationException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedOperationException If the module defines no higher-order operators.
	 */
	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException, IllegalArgumentException;
	
	/**Get the meta-data about a specific operator, given the specializer.
	 * 
	 * TODO: Replace "specializer" with "context" object
	 * 
	 * @throws IllegalArgumentException Name passed is not known by this module.
	 * @throws SpecializationException Specializer passed not permitted with the given operator.
	 * */
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException,IllegalArgumentException ;

	/**Get the meta-data object describing the module.*/
	public ModuleData getModuleData();
	
	public String getName();
}
