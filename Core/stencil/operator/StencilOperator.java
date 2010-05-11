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
package stencil.operator;


import stencil.operator.module.util.OperatorData;
import stencil.operator.util.Invokeable;


/**An operator is an object that can 
 * hand out method objects to be appropriately invoked.
 * Method objects are requested by role (role matching is case
 * insensitive).  
 * 
 * Required roles: map and query
 * Canonical, optional roles:
 * 		guide -- Used in guide generation, may modify future results of map
 *  			as guide requests may represent advice of potential inputs;
 *  			when guide is not available query is used instead.
 * 		updateRequired -- Used to determine if a dynamic binding or guide needs to be updated.
 * 				Should return true if a call to map/query/guide may return 
 * 				a different value than it did previously.  If not available,
 * 				this will be assumed false on functions and true on non-functions.
 * 
 * 
 * The method objects returned by a role request must return a Tuple, 
 * but may take any of a number of arguments over any types
 * (and thus don't conform to a standard java Interface).  
 * The Stencil runtime will take care of properly constructing
 * an argument array to invoke the given method.
 * 
 * Operators instances may be specialized, and the specialization will
 * be reflected in all roles.  Operators instances of the same base type
 * are completely independent. 
 * 
 * @author jcottam
 *
 */
public interface StencilOperator {
	//TODO:Move these names to ParserConstants...
	/**Name of the facet used by default in contexts where mutation is permitted.*/
	public static final String MAP_FACET ="map";

	/**Name of the facet used by default in contexts where mutation is NOT permitted.*/
	public static final String QUERY_FACET ="query";
	
	/**Suggested facet name for use in ranged operations using Stencils range helpers.*/
	public static final String RANGE_FACET ="range";
	
	/**Facet used to get the ID of the current state.
	 * This is used to determine if update operations are required.
	 */
	public static final String STATE_ID_FACET = "stateID";
	
	/**Retrieve an invokable object.  This is a combined method and target.
	 * IllegalArgumentException is thrown when the facet is not know.
	 * */
	public Invokeable getFacet(String facet) throws IllegalArgumentException;
	
	/**Retrieve the operator data for the current operator.*/ 
	public OperatorData getOperatorData();
	
	/**How is this legend identified?  Legends are registered in their modules under their name.*/
	public String getName();
	
	/**Return a new operator that is functionally identical, but with Stencil-runtime state
	 * reset.  Any specialization-determined state should be copied to the new operator.
	 * 
	 * This is an optional operation, required by Split support.  If an operator does not support
	 * split, it should throw an UnsupportedOperationException.
	 * 
	 * This is similar, but not identical to clone as clone copies ALL runtime state where
	 * duplicate produces a new instance.
	 * 
	 * A possible implementation would be to clone the implementing object immediately upon
	 * 	construction and keep that pristine clone in private storage.  When duplicate is
	 * called, the clone is then cloned again and the new replicate is returned.  
	 * This is not necessarily an efficient way to implement duplicate, but it is
	 * would satisfy the semantics of duplicate. True mathematical functions with no 
	 * specialization arguments may return themselves from this method (no duplication required). 
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public StencilOperator duplicate() throws UnsupportedOperationException;
}
