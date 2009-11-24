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
package stencil.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;



/***
 * Principally a unit for passing information around.  
 * Conceptually, this is a map from names to values,
 * but with some special properties.
 *   
 * @author jcottam
 */
public final class PrototypedTuple implements Tuple {
	protected List<String> names;
	protected List values;
	
	/**Create a Tuple with a single value in it.  If key is left unspecified, the default key is used.*/
	public static PrototypedTuple singleton(Object value) {
		return singleton(DEFAULT_KEY, value);
	}
	
	public static PrototypedTuple singleton(String key, Object value) {
		return new PrototypedTuple(Arrays.asList(new String[]{key}), Arrays.asList(value));
	}

	/**Create a new tuple.
	 *
	 * @param source Name of the stream it came from.
	 * @param names Names of the values present in the tuple.
	 * @param values Values to be stored in the tuple.
	 */
	public PrototypedTuple(String source, String[] names, Object[] values) {
		this(source, Arrays.asList(names), Arrays.asList(values));
	}
	
	/**Create a new tuple.
	 *
	 * @param source Name of the stream it came from.
	 * @param names Names of the values present in the tuple.
	 * @param values Values to be stored in the tuple.
	 */
	public PrototypedTuple(String source, List<String> names, List values) {
		this(appendValue(names, SOURCE_KEY), appendValue(values, source));
	}

	//Appends a value to a list, allocating an append-able list if needed
	private static List appendValue(List list, Object value) {
		if (!(list instanceof ArrayList)) {list = new ArrayList(list);} //Allocates a new list if the old one can't be appended to
		list.add(value);
		return list;
	}
	
	
	/**Create a new tuple.
	 *
	 * @param names Names of the values present in the tuple.
	 * @param values Values to be stored in the tuple.
	 */
	public PrototypedTuple(String[] names, Object[] values) {this(Arrays.asList(names), Arrays.asList(values));}
	public PrototypedTuple(List<String> names, List values) {
		assert names != null : "Names may not be null.";
		assert values != null : "Values may not be null.";
		assert names.size() == values.size() : "Value and name list not of the same length." + names + " vs. " + values;
		assert findDuplicateName(names) ==  null : "Duplicate name found in names list: " + findDuplicateName(names);
		
		this.names = Collections.unmodifiableList(names);
		this.values = values;
	}

	/**Verify that the names list contains no duplicates.*/
	private static final String findDuplicateName(List<String> names) {
		String[] ns = names.toArray(new String[names.size()]);
		Arrays.sort(ns);		
		for (int i =0; i<ns.length-1; i++) {
			if (ns[i].equals(ns[i+1])) {return ns[i];}
		}
		return null;
	}
	

	/**Returns a string as-per the static toString() method.**/
	public String toString() {return Tuples.toString(this);}

	public List<String> getPrototype() {return names;}
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}
	public Object get(int idx) {return values.get(idx);}
	public int size() {return values.size();}

	public boolean isDefault(String name, Object value) {return false;}
}

