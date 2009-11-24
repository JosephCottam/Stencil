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
package stencil.adapters.java2D.util;

import java.util.*;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.tuple.InvalidNameException;

public final class AttributeList implements Iterable<Attribute>{
	private final Map<String, Attribute> attributes;
	private List<String> names = null;

	public AttributeList() {attributes = new TreeMap();}
	
	public AttributeList(AttributeList prior) {
		this();
		for (Attribute a: prior) {
			attributes.put(a.name, a);
		}
	}
	
	/**Add a new attribute.  
	 * Will silently replace an existing attribute of the same name.
	 */
	public void add(Attribute att) {
		attributes.put(att.name, att);
		names = null;
	}
	
	public int size() {return attributes.size();}
	
	/**Get the nth item from the attribute list.
	 * Order is stable as long as the attribute listing does not change.
	 * @param idx
	 * @return
	 */
	public Attribute get(int idx) {return attributes.get(names.get(idx));}
	
	public Attribute get(Enum name) {return get(name.name());}
	
	/**Get the given attribute.*/
	public Attribute get(String name) {
		return attributes.get(name);
	}

	/**Get an iterator over all of the attributes.*/
	public Iterator<Attribute> iterator() {
		return attributes.values().iterator();
	}
	
	/**Get a list of all names in this attributes collection.*/
	public List<String> getNames() {
		if (names == null) {
			names = new ArrayList(attributes.keySet());
		}
		return names;
	}
	
	/**Get the default value of the attribute of the given  name
	 * @throws InvalidNameException Requested name not found in this collection
	 */
	public Object getDefault(String name) throws InvalidNameException {
		Attribute att = attributes.get(name);
		if (att == null) {throw new InvalidNameException(name);}
		return att.defaultValue;
	}
	
	/**Ensures that an attribute no longer exists in this collection.*
	 * If the attribute never existed, no exception is thrown.
	 */
	public void remove(String name) {
		if (attributes.containsKey(name)) {attributes.remove(name);}
	}
	
	public void remove(StandardAttribute att) {remove(att.name());}
}
