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
package stencil.streams;

import java.util.ArrayList;
import java.util.List;

public interface Tuple {
	/**Tuple with no fields.  Should be used instead of null wherever a tuple 
	 * is required but cannot be supplied.*/
	public static final Tuple EMPTY_TUPLE = new Tuple() {
		public Object get(String name) throws InvalidNameException {throw new InvalidNameException(name);}
		public Object get(String name, Class<?> type)
				throws IllegalArgumentException, InvalidNameException {throw new InvalidNameException(name);}
		public List<String> getFields() {return new ArrayList<String>();}
		public boolean hasField(String name) {return false;}
		public boolean isDefault(String name, Object value) {throw new InvalidNameException(name);}
		
	};
	
	public static final String DEFAULT_KEY = "VALUE";
	public static final String SOURCE_KEY = "SOURCE";

	/**Is this field known by this tuple?*/
	public abstract boolean hasField(String name);

	/**Get a listing of all fields known by this tuple (even if they are not set).
	 * The order of the list corresponds to the index of the field number.
	 * */
	public abstract List<String> getFields();

	/**Returns the object as stored under the name.
	 * @throws InvalidNameException The name passed is not valid for this tuple.*/
	public abstract Object get(String name) throws InvalidNameException;

	/**Returns the object stored under the name, in a form that may be cast to the type.
	 * Types guaranteed to work are limited to the storage type and the types with designated getters.
	 * If the item stored under the given name cannot be cast to the given type, an IllegalArgumentException should be thrown.*/
	public abstract Object get(String name, Class<?> type) throws IllegalArgumentException, InvalidNameException;

	/**Is this the default value for this field?*/
	public abstract boolean isDefault(String name, Object value);


}