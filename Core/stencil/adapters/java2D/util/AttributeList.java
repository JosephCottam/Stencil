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

import stencil.streams.InvalidNameException;

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
	
	public void add(Attribute att) {
		attributes.put(att.name, att);
		names = null;
	}
	
	public Attribute get(String name) {
		return attributes.get(name);
	}

	public Iterator<Attribute> iterator() {
		return attributes.values().iterator();
	}
	
	public List<String> getNames() {
		if (names == null) {
			names = new ArrayList(attributes.keySet());
		}
		return names;
	}
	
	public Object getDefault(String name) throws InvalidNameException {
		Attribute att = attributes.get(name);
		if (att == null) {throw new InvalidNameException(name);}
		return att.defaultValue;
	}
}
