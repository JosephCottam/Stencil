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


import stencil.parser.tree.Value;
import stencil.tuple.Tuple;

import java.util.List;


/**A Legend is a group of mapping function that may be specialized
 * to serve separate instance independently.  BasicLegend dictates the minimum
 * methods that must exist for a class to be used as a Legend.  Module-provided
 * meta-data reports the remaining information to the Stencil system.
 * 
 * Individual methods on a Legend are its mapping functions.
 *
 * The simplest legend is the identity function (tree = results).  Legends will typically move input values
 * to a color space (like a heat-map), coordinate space (alphabetical ordering on the axis)
 * or a shape space.
 * 
 * A more complex map is the keystore.  It has two methods: map and query that behave differently,
 * but rely on information shared between them.  Futhermore, the 'remove' method modifies
 * the map and query behavior more, but is not a standard legend function.  
 *
 * @author jcottam
 *
 */
public interface StencilOperator {
	/**Name of the method that should be invoked by clients wanting to use this legend*/
	public static final String INVOKE_METHOD ="Map";
	public static final String QUERY_METHOD ="Query";

	/**Given the objects passed, what is the resulting object?
	 * This method may feel free to produce side-effects.
	 * The source for the returned tuple should be the same as the name
	 * returned by getName.
	 */
	public Tuple map(Object... args);

	/**Alternative interface for map, but side effects are strongly discouraged.
	 * This is used to check if something has been seen before, or
	 * a hypothetical query before a set of values is accepted.
	 * Generally, query(X) = map(X) when X is a valid set of objects.
	 * However, query should return a tuple to indicate failure where
	 * map is allowed to throw exceptions.   Generally, the tuple
	 * should contain a single 'null' value, however if 'null'
	 * "makes sense" for the legend, the implementing legend
	 * should provide a consistent indicator.
	 */
	public Tuple query(Object... args);

	/**How is this legend identified?  Legends are registered in their modules under their name.*/
	public String getName();
	
	/**Return a new operator that is functionally identical, but with Stencil-runtime state
	 * reset.  Any specialization-determined state should be copied to the new operator.
	 * 
	 * This is an optional operation, required by Split support.  If an operator does not support
	 * split, it should throw an UnsupportedOperationException.
	 * 
	 * This is similar, but not identical to clone.
	 * A possible implementation would be to clone the implementing object immediately upon
	 * construction and keep that pristine clone in private storage.  When duplicate is
	 * called, the clone is then cloned again and the new replicate is returned.  
	 * This is not necessarily an efficient way to implement duplicate, but it is
	 * would satisfy the semantics of duplicate. True mathematical functions with no 
	 * specialization arguments may return themselves from this method (no duplication required). 
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public StencilOperator duplicate() throws UnsupportedOperationException;
	

	/**Generate the relevant guide objects.
	 * 
	 * For categorical operations, all inputs may be ignored and a list of categories is returned.  
	 * 
	 * For project operations, the input is a list of potential requests to map.
	 * The output is a list of results that the operator would produce
	 * if presented with those inputs.
	 * 
	 * @param formalArguments What are the formal arguments for this operator invocation instance?
	 * @param sourceArguments What was produced by the prior operator in the chain?
	 * @param prototype What is the tuple prototype for the prior operator in the chain? //TODO: Remove when the formals use strictly positional references
	 * 
	 * @throws IllegalArgumentException Null list passed to project operation
	 * @throws IllegalArgumentException Non-null list passed to categorize operation
	 * @throws UnsupportedOperationException Thrown when this method is not defined
	 */
	public List<Object[]> guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype);
	
	/**Does this operator require a refresh for the guide?
	 * Returns true when the guide operator would likely return
	 * different results given the same input than before.
	 **/
	public boolean refreshGuide();
}
