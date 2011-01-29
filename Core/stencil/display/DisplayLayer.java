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
package stencil.display;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface DisplayLayer<T extends Glyph>  {
	/**Name of the method that should be invoked by clients wanting to use this operator*/
	public static final String FIND_METHOD  = "find";
	public static final String MAKE_METHOD = "make";
	public static final String MAKE_OR_FIND_METHOD ="makeOrFind";
	public static final String TYPE_KEY = "type";

	
	/**Get a view of this layer.
	 * A view is a consistent look at the state of a layer.
	 * */
	public LayerView<T> getView();
	
	/**Creates a new item of type T in this layer.
	 * The properties of the new item are dictated by the values
	 * passed.  This must include an ID field.
	 *  
	 * @throws IDException Thrown when the ID already exists in the layer
	 * */
	public Glyph make(Tuple values) throws IDException;

	/**Returns the item associated with the name on this layer.
	 * Returns null if no item is associated.*/
	public Glyph find(String ID);

	/**Returns an item in the layer.
	 * 
	 * If an item with the given ID already exists, that item
	 * is updated with the values passed. Otherwise, the item is created and
	 * then updated.  
	 * 
	 * Values passed must include at least an ID field.
	 * 
	 * @param ID ID to search under
	 * @return Tuple with the given ID
	 */
	public Glyph makeOrFind(Tuple values);
	
	/**Update an existing tuple in the table.
	 * The update tuple must have an ID that is already
	 * present in the table. 
	 * 
	 * @throws IllegalArgumentException Update ID not found in table.
	 * 
	 */
	public void update(T update) throws IllegalArgumentException;

	/**Given an ID, remove the associated tuple from the layer*/
	public void remove(String ID);

	/**Is the provided ID associated with a tuple on this layer?*/
	public boolean contains(String ID);
	
	/**What is the name of this layer*/
	public String getName();

	/**Identifier for the current state**/
	public int getStateID();
	
	/**What is the tuple prototype for this layer?
	 * This is derived directly from the implantation type.
	 */
	public TuplePrototype getPrototype();
	public void updatePrototype(Tuple defaults);
	
	/**Register the input values with the given glyph.*/
	public void addDynamic(int groupID, T g, Tuple t);
}
