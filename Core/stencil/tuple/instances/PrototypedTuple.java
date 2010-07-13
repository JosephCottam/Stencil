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
package stencil.tuple.instances;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.TypeValidationException;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

import stencil.types.Converter;



/***
 * Principally a unit for passing information around.  
 * Conceptually, this is a map from names to values,
 * but with some special properties.
 *   
 * @author jcottam
 */
public final class PrototypedTuple implements Tuple {
	protected TuplePrototype prototype;
	protected Object[] values;
	
	/**Create a Tuple with a single value in it.  If key is left unspecified, the default key is used.*/
	public static PrototypedTuple singleton(Object value) {
		return singleton(DEFAULT_KEY, value);
	}
	
	public static PrototypedTuple singleton(String key, Object value) {
		return new PrototypedTuple(new String[]{key}, new Object[]{value});
	}
	
	public static PrototypedTuple singleton(String key, Class type, Object value) {
		return new PrototypedTuple(Arrays.asList(key), Arrays.asList(type), new Object[]{value});
	}
	
	/**Create a new tuple.
	 *
	 * @param names Names of the values present in the tuple.
	 * @param values Values to be stored in the tuple.
	 */
	public PrototypedTuple(List<String> names, List values) {this(names.toArray(new String[0]), values.toArray());}
	public PrototypedTuple(String[] names, Object[] values) {this(names, TuplePrototypes.defaultTypes(names.length), values);}
	public PrototypedTuple(List<String> names, List<Class> types, List<Object> values) {this(names, types, values.toArray());}
	
	public PrototypedTuple(List<String> names, List<Class> types, Object[] values) {this(names.toArray(new String[0]), types.toArray(new Class[types.size()]), values);}
		public PrototypedTuple(String[] names, Class[] types, Object[] values) {
		assert types != null : "Types may not be null";
		assert names != null : "Names may not be null.";
		assert names.length == values.length : "Value and name list not of the same length." + names + " vs. " + values;
		assert findDuplicateName(names) ==  null : "Duplicate name found in names list: " + findDuplicateName(names);

		this.prototype = new SimplePrototype(names, types);
		this.values = validate(types, values);
	}
	
	public PrototypedTuple(TuplePrototype prototype, List values) {
		this (prototype, values.toArray(), false);
	}

	public PrototypedTuple(TuplePrototype prototype, Object[] values) {
		this (prototype, values, false);
	}
	
	public PrototypedTuple(TuplePrototype prototype, Object[] values, boolean doConversions) {
		this.prototype = prototype;
		if (doConversions) {
			this.values = validate(TuplePrototypes.getTypes(prototype), values);
		} else {
			this.values = values;
		}
	}

	
	private static final Object[] validate(Class[] types, Object[] values) {
		if (types.length != values.length) {throw new TypeValidationException("Type list and value list are of different lengths");}
		Object[] newValues = new Object[values.length];
		
		for (int i=0; i< types.length; i++) {
			Class target = types[i];
			Object value = values[i];
			if (!target.isInstance(value)) {
				try {newValues[i] = Converter.convert(value, target);}
				catch (Exception e) {throw new TypeValidationException(types[i], values[i], e);}
			} else {
				newValues[i] = value;
			}
		}
		return newValues;
	}

	/**Verify that the names list contains no duplicates.*/
	private static final String findDuplicateName(String[] names) {
		String[] ns = new String[names.length];
		System.arraycopy(names, 0, ns, 0, names.length);
		Arrays.sort(ns);		
		for (int i =0; i<ns.length-1; i++) {
			if (ns[i].equals(ns[i+1])) {return ns[i];}
		}
		return null;
	}
	

	/**Returns a string as-per the static toString() method.**/
	public String toString() {return Tuples.toString(this);}

	public TuplePrototype getPrototype() {return prototype;}
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}
	public Object get(int idx) {return values[idx];}
	public int size() {return values.length;}

	public boolean isDefault(String name, Object value) {return false;}
}

