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


import java.util.*;

import stencil.adapters.general.Fills;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.SIGIL;

/**Utility methods for working with tuples.*/
//final because it is a collection of utility methods and is not to be instantiated or overridden
public final class Tuples {
	private Tuples() {/*Utility class. Not instantiable.*/}

	/**Tuple with no fields.  Should be used instead of null wherever a tuple 
	 * is required but cannot be supplied.*/
	public static final Tuple EMPTY_TUPLE = new Tuple() {
		public Object get(String name) throws InvalidNameException {throw new InvalidNameException(name);}
		public Object get(int idx) {throw new TupleBoundsException(idx, size());}
		public TuplePrototype getPrototype() {return new SimplePrototype();}
		public int size() {return 0;}
		public boolean isDefault(String name, Object value) {throw new InvalidNameException(name);}
		public String toString() {return "EMPTY TUPLE";}
	};
	
	/**Placeholder object that indicats that a value in a tuple
	 * was not set.  Since 'null' is a valid value in some places,
	 * this is used in some circumstances to indicate that a value was not explicitly set.
	 */
	public static final Object UNSET_FIELD = new Object() {public String toString() {return "Unset Field: " + super.toString();}};

	
	/**Given a source tuple, returns a new tuple with only those fields that were
	 * prefixed with the given prefix.  Fields no longer have the prefix.
	 */
	public static final Tuple sift(String prefix, Tuple source) {
		List<String> fields = new ArrayList();
		List<Object> values = new ArrayList();
		
		if (source == null) {return EMPTY_TUPLE;}
		
		for (String field:TuplePrototypes.getNames(source)) {
			if (field.startsWith(prefix)) {
				values.add(source.get(field));
				fields.add(field.substring(prefix.length()));
			}
		}
		
		return new PrototypedTuple(fields, values);
	}

	
	/**Creates a read-only copy of the given tuple.  Values
	 * are copied per the rules defined in Transfer.
	 *
	 * If the source is null, the copy will be the cannonical empty tuple.
	 *
	 * @param source
	 * @return
	 */
	public static Tuple copy(Tuple source) {
		if (source == null) {return Tuples.EMPTY_TUPLE;}
		
		List<String> attributes =  new ArrayList<String>();
		attributes.addAll(Arrays.asList(TuplePrototypes.getNames(source)));

		Object[] values = new Object[attributes.size()];
		for (int i=0; i< attributes.size(); i++) {
			Object v = source.get(attributes.get(i));
			if (v != null) {values[i] = v;}
		}

		return new PrototypedTuple(attributes, Arrays.asList(values));
	}

	/**Copies all of the field of the source to the target.
	 * Only value that appear in the 'getFields' list of the source
	 * will be copied (so implicit fields on concrete tuples, like glyphs,
	 * must be reported in getFields for this to work).
	 * This is a shallow copy, as not all objects can be cloned.
	 * 
	 * Errors in transfer are ignored and not reported.  The attribute is simply skipped.
	 * This is probably not the right way to do it...but it is my way...
	 *
	 * @param source Source of values.
	 * @param target Target for values.  Existing values may be overwritten.
	 * @param defaults Should default values be transfered (false will skip any value currently set to the default on the source)
	 * @return The target passed in (after the transfer).
	 */
	public static final MutableTuple transfer(Tuple source, MutableTuple target) {
		for (String field:TuplePrototypes.getNames(source)) {
			Object sourceValue = source.get(field);
			Object targetValue = target.get(field);
			if (sourceValue == targetValue || sourceValue.equals(targetValue)) {continue;}	//Skip values that are equal.
			try {if (!source.isDefault(field, sourceValue)) {target.set(field, sourceValue);}}
			catch (Exception e) {target.set(field, sourceValue);}	//If there was an error, assume it was not the default value
		}
		return target;
	}

	/**Compares two tuples.  Tuples are considered transferNeutral if
	 * the target tuple would be functionally equivalent after a transfer from the source.
	 * This is done by testing the transferable values with .equals
	 * (which is supposed to be symmetric, but may not be, so the target's value's
	 * .equals is invoked on the source's value).
	 *
	 * If the source has any field not included in the target, it will
	 * automatically not be transferNeutral.
	 *
	 * @return
	 */
	public static boolean transferNeutral(Tuple source, Tuple target) {
		List<String> targetFields = Arrays.asList(TuplePrototypes.getNames(target));
		String[] sourceFields = TuplePrototypes.getNames(source);
		for (String field: sourceFields) {
			if (!targetFields.contains(field)) {return false;}
			Object sourceValue = source.get(field);
			Object targetValue = target.get(field);
			if (targetValue == sourceValue ||
				(targetValue != null && !targetValue.equals(sourceValue))) {return false;} //TODO: Why is there this "TargetValue != null" ??? IS this part of layer attribute defaulting?  This messes up some things.
		}
		return true;
	}

	/**Strings of tuples look like name/value lists where
	 * the name and value are separated with a colon and the next
	 * values are terminated with a semicolon and a space.
	 * This static method can be used with any tuple.
	 *
	 * This method goes through extra work to ensure a consistent tuple
	 * representation.  As such, fields are always printed out in
	 * alphabetical order when using this method.
	 *
	 * **/
	public static String toString(Tuple t) {return toString(t, TuplePrototypes.getNames(t));}
	
	/**String tuple representation with a prefix.  
	 * This mimics the sigil operator style: @<prefix>{<values>}.
	 * 
	 * @param prefix Type prefix
	 * @param t
	 * @return
	 */
	public static String toString(String prefix, Tuple t, int start) {
		String[] names = TuplePrototypes.getNames(t);
		String[] printNames = new String[names.length - start];
		System.arraycopy(names, start, printNames, 0, printNames.length);
		
		String base = toString(t, printNames);
		base = base.substring(1, base.length()-1);
		return SIGIL + prefix + "{" + base + "}";
	}
	
	/**Provide a string representation of the tuple, 
	 * but only include the fields in the passed list.
	 */
	public static String toString(Tuple t, String[] fields) {
		StringBuilder rv = new StringBuilder("(");
		
		for (String name: fields){
			Object value =t.get(name);
			if (value instanceof Number && ((Number) value).doubleValue() ==-0) {value =0.0d;}//Prevent negative zeros
			
			//Skip values currently set to the default
			if (t.isDefault(name, value)) {continue;}
			
			if (value == null) {
				value = "[null]";
			} else if (value instanceof java.awt.TexturePaint) {
				value = Fills.fillString((java.awt.TexturePaint) value);
			}else if (value.getClass().isArray()) {
				value = Arrays.deepToString((Object[]) value);
			} else if (value == t) {
				value = "<self>";
			} else {
				value = Converter.toString(value);
			}
			
			rv.append(String.format("%1$s:%2$s; ", name, value));
		}

		rv.deleteCharAt(rv.length()-1);
		if (rv.length() == 0) {rv.append("()");}
		else {rv.append(")");}
		return rv.toString();
	}

	/**Create a new tuple with fields representing a union of the
	 * fields of the two source tuples.  Values will be taken from
	 * source1 first, then source2 (so last-write wins on shared fields).
	 *
	 * If source1 is null or source2 is null, the other is returned.
	 * Since a copy is not made when merging to a null tuple, this 
	 * is not always safe to use with mutable tuples.  If safety must 
	 * be guaranteed, then copy should be used on the arguments.
	 *
	 * TODO: Replace all calls to merge with calls to append
	 *
	 * @param sourceName Where should the resulting tuple indicate it is from?
	 * @param source1
	 * @param source2
	 * @return
	 */
	public static Tuple merge(Tuple source1, Tuple source2) throws IllegalArgumentException {
		if (source1 == null && source2 ==null) {throw new IllegalArgumentException("At least one source to merge must not be null.");}

		if (source1 == null || source1 == EMPTY_TUPLE) {return source2;}
		if (source2 == null || source2 == EMPTY_TUPLE) {return source1;}
		if (source1 == source2) {return source1;}

		Map<String, Object> store = new HashMap();
		
		for (String name: TuplePrototypes.getNames(source1)) {
			Object value = source1.get(name);
			store.put(name, value);
		}
		for (String name: TuplePrototypes.getNames(source2)) {
			Object value = source2.get(name);
			store.put(name, value);
		}
		
		List<String> names = new ArrayList(store.keySet());
		List<Object> values = new ArrayList(store.values()); 
		
		return new PrototypedTuple(names, values);
	}
	
	/**Produces an array version of a tuple.  Value are in the same order as the original tuple fields.**/
	public static Object[] toArray(Tuple t) {
		Object[] values = new Object[t.size()];
		for (int i=0; i< values.length; i++) {values[i] = t.get(i);}
		return values;
	}
	
	/**A straightforward way of doing named de-reference on a tuple.
	 * 
	 * Specific tuple types may have faster ways of performing a named de-reference,
	 * however this method uses only the features of the Tuple interface and
	 * may as such be used with any Tuple.
	 * 
	 * @param name
	 * @param source
	 * @return
	 */
	public static final Object namedDereference(String name, Tuple source) {
		int idx = source.getPrototype().indexOf(name);
		if (idx >=0) {return source.get(idx);}
		throw new InvalidNameException(name, source.getPrototype());
	}

	/**Given a tuple and a prototype, re-arranges the values so the indexes of
	 * the source match the indexes of the names.
	 */
	public static Tuple align(Tuple source, TuplePrototype prototype) {
		Object[] values = new Object[prototype.size()];
		for (int i=0; i< values.length; i++) {
			String name = prototype.get(i).getFieldName();
			values[i] = source.get(name);
		}
		return new ArrayTuple(values);
	}
	
	public static Tuple[] alignAll(final Tuple[] sources, TuplePrototype prototype) {
		Tuple[] results = new Tuple[sources.length];
		for (int i=0; i< sources.length; i++) {
			results[i] = align(sources[i], prototype);
		}
		return results;
	}
	
	/**Get a value from the passed tuple; return null if the field is not present in the tuple.*/
	public static final <T> T safeGet(String field, Tuple source, TuplePrototype p, T def) {
		return safeGet(field, source, p, (Class<T>) def.getClass(), def);
	}

	public static final <T> T safeGet(String field, Tuple source, TuplePrototype p, Class<T>  type, T def) {
		return (!source.getPrototype().contains(field)) ? def : (T) Converter.convert(source.get(field), type);
	}
	
}
