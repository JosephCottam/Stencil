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
package stencil.operator.module;

import java.util.Collection;
import stencil.parser.tree.Specializer;

/**Interface for an object providing meta-data about a legend.
 * 
 * A legend does not exist until it is instantiated, so this 
 * represents the meta data for a particular instance of a legend,
 * not the general class of legends.  This data may change between
 * instances based on different specializers used in creating
 * the instance.
 * */
public interface OperatorData {
	public static enum OpType {PROJECT, CATEGORIZE, BOTH, NA};
	
	/**What is the legend's name?*/
	public String getName();
	
	/**What module did it come from?  Null means no permanent module.*/
	public String getModule();

	/**What facets exist can be invoked?*/
	public Collection<String> getFacets();

	/**Get the facet data object for the named facet.
	 * */
	public FacetData getFacetData(String name);
	
	public Specializer getDefaultSpecializer();
	
	/**Retrieve the named attribute.
	 * Unknown attributes must all default to null.*/
	public String getAttribute(String name);
	
	/**What are the valid attributes*/
	public Collection<String> getAttributes();
	
	/**Does this operator data object contain any values not-yet-determined
	 * (usually all value are determined with a complete specializer)?
	 */
	public boolean isComplete();
}
