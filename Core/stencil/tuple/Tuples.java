package stencil.tuple;


import java.util.*;

import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.util.collections.ListSet;
import static stencil.parser.ParserConstants.NAME_SEPARATOR_PATTERN;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;
import static stencil.parser.ParserConstants.SIGIL;

/**Utility methods for working with tuples.*/
//final because it is a collection of utility methods and is not to be instantiated or overridden
public final class Tuples {
	private Tuples() {/*Utility class. Not instantiable.*/}

	/**Tuple with no fields.  Should be used instead of null wherever a tuple 
	 * is required but cannot be supplied.*/
	public static final PrototypedTuple EMPTY_TUPLE = new PrototypedArrayTuple(new String[0], new Object[0]);
	
	/**Place-holder object that indicates that a value in a tuple
	 * was not set.  Since 'null' is a valid value in some places,
	 * this is used in some circumstances to indicate that a value was not explicitly set.
	 */
	public static final Object UNSET_FIELD = new Object() {public String toString() {return "Unset Field: " + super.toString();}};

	
	/**Given a tuple with prefixed fields, creates a tuple with sub-tuples.
	 * Each field in the original that shared a prefix is now stored as part of a field with the prefix name.
	 * The resulting tuple will have root fields first, then the prefix-based tuples in the order of the prefixes.
	 */
	public static final PrototypedTuple restructure(PrototypedTuple source, String... prefixes) {
		PrototypedTuple root = sift(source, null);
		ArrayList<Tuple>  subs = new ArrayList(prefixes.length);
		for(String prefix: prefixes) {
			subs.add(sift(source, prefix));
		}
		return Tuples.merge(root, new PrototypedArrayTuple(prefixes, subs.toArray()));
	}

	/**Returns a tuple where all field names are prefixed by the passed prefix.**/
	public static final PrototypedTuple prefix(PrototypedTuple source, String prefix) {
		List<Object> values = new ArrayList();
		List<TupleFieldDef> defs = new ArrayList();
		TuplePrototype proto = source.prototype();
		
		for (int i=0; i<  source.size(); i++) {
			values.add(source.get(i));
			TupleFieldDef def = proto.get(i);
			def = def.rename(prefix + NAME_SEPARATOR + def.name());
			defs.add(def);
		}
		return new PrototypedArrayTuple(new TuplePrototype(defs.toArray(new TupleFieldDef[defs.size()])), values);
	}
	
	
	/**Given a source tuple, returns a new tuple with only those fields that were
	 * prefixed with the given prefix.  Fields no longer have the prefix.
	 * 
	 * If prefix passed is null, then only values WITHOUT a prefix are returned.
	 */
	public static final PrototypedTuple sift(PrototypedTuple source, String prefix) {
		List<String> fields = new ArrayList();
		List<Object> values = new ArrayList();
		
		if (source == null) {return EMPTY_TUPLE;}
		
		for (String field: TuplePrototypes.getNames(source)) {
			String[] parts = field.split(NAME_SEPARATOR_PATTERN);
			if (prefix == null && parts.length == 1) {
				values.add(source.get(field));
				fields.add(field);
			} else if (prefix != null && parts.length == 1) {
				continue;		//looking for a prefix, but none found, move along
			} else if (prefix != null && parts[0].equals(prefix)) {
				values.add(source.get(field));
				fields.add(field.substring(field.indexOf(NAME_SEPARATOR)+1));				
			}
		}
		return new PrototypedArrayTuple(fields, values);
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
	public static String toString(Tuple t) {
		if (t instanceof PrototypedTuple) {
			return toString((PrototypedTuple) t, TuplePrototypes.getNames((PrototypedTuple) t));
		} else {
			return toStringByIndex(t);
		}
	}

	/**String tuple representation with a prefix.  
	 * This mimics the sigil operator style: @<prefix>{<values>}
	 * 
	 * @param prefix Type prefix
	 * @param t
	 * @return
	 */
	public static String toString(String prefix, PrototypedTuple t, int start) {
		String[] names = TuplePrototypes.getNames(t);
		String[] printNames = new String[names.length - start];
		System.arraycopy(names, start, printNames, 0, printNames.length);
		
		String base = toString(t, printNames);
		base = base.substring(1, base.length()-1);
		return SIGIL + prefix + "{" + base + "}";
	}
	
	
	private static String toStringByIndex(Tuple t) {
		StringBuilder rv = new StringBuilder("(");
		for (int i=0; i<t.size(); i++) {
			String value = formatValue(t, t.get(i));
			rv.append(String.format("**%1$s:%2$s; ", i, value));
		}

		rv.deleteCharAt(rv.length()-1);
		if (rv.length() == 0) {rv.append("()");}
		else {rv.append(")");}
		return rv.toString();
	}
	

	
	/**Provide a string representation of the tuple, 
	 * but only include the fields in the passed list.
	 */
	public static String toString(PrototypedTuple t, String[] fields) {
		StringBuilder rv = new StringBuilder("(");
		
		for (String name: fields){
			Object value;
			try {value =t.get(name);} 
			catch (Exception e) {value = "ERROR: " + e.getMessage();}
			
			if (value instanceof Number && ((Number) value).doubleValue() ==-0) {value =0.0d;}//Prevent negative zeros

			value = formatValue(t, value);
			rv.append(String.format("%1$s:%2$s; ", name, value));
		}

		rv.deleteCharAt(rv.length()-1);
		if (rv.length() == 0) {rv.append("()");}
		else {rv.append(")");}
		return rv.toString();
	}

	private static final String formatValue(Tuple source, Object value) {
		
		if (value == null) {
			return "[null]";
		}else if (value.getClass().isArray()) {
			return Arrays.deepToString((Object[]) value);
		} else if (value == source) {
			return "<self>";
		} else {
			return Converter.toString(value);
		}
	}
	
	//Internal flag to indicate that there is no skip value provided
	private static final Object NO_SKIP = new Object();

	/**Complete merge, no skip value.**/
	public static PrototypedTuple merge(PrototypedTuple source1, PrototypedTuple source2) throws IllegalArgumentException {
		return merge(source1, source2, NO_SKIP);
	}


	public static PrototypedTuple mergeAll(PrototypedTuple... tuples) {
		if (tuples.length ==0) {return null;}
		PrototypedTuple target = tuples[0];
		for (int i=1; i< tuples.length; i++) {
			target = merge(target, tuples[i]);
		}
		return target;
	}
	
	/**Create a new tuple with fields representing a union of the
	 * fields of the two source tuples.  Values will be taken from
	 * source1 first, then source2 (so last-write wins on shared fields).
	 *
	 * If source1 is null or source2 is null, the other is returned.
	 * Since a copy is not made when merging to a null tuple, this 
	 * is not always safe to use with mutable tuples.  If safety must 
	 * be guaranteed, then copy before calling merge.
	 *
	 * To make results prototypes more predictable, the field ordering of tuple1 is always preserved
	 * and any fields of tuple2 not in tuple1 are appended to the end in the order they appear in tuple2.
	 *
	 * @param sourceName Where should the resulting tuple indicate it is from?
	 * @param source1 Initial tuple 
	 * @param source2 New tuple 
	 * @param skipValue Value in source2 that indicates the value of source1 should be retained
	 * @return
	 */
	public static PrototypedTuple merge(PrototypedTuple source1, PrototypedTuple source2, Object skipValue) throws IllegalArgumentException {
		if (source1 == null && source2 ==null) {throw new IllegalArgumentException("At least one source to merge must not be null.");}

		if (source1 == null || source1 == EMPTY_TUPLE) {return source2;}
		if (source2 == null || source2 == EMPTY_TUPLE) {return source1;}
		if (source1 == source2) {return source1;}

		ListSet<String> defs = new ListSet();
		List vals = new ArrayList();
		
		for (Object f: source1.prototype()) {
			TupleFieldDef field = (TupleFieldDef) f;
			defs.add(field.name());
			vals.add(source1.get(field.name()));
		}
		for (Object f: source2.prototype()) {
			TupleFieldDef field = (TupleFieldDef) f;
			Object value = source2.get(field.name());
			if (skipValue.equals(value)) {continue;}
			int idx = defs.indexOf(field.name());
			if  (idx <0) {
				defs.add(field.name());
				vals.add(value);
			} else {
				vals.set(idx, value);
			}
		}
		
		String[] names = defs.toArray(new String[defs.size()]);
		return new PrototypedArrayTuple(names, vals.toArray());
	}
	
	/**Produces an array version of a tuple.  Value are in the same order as the original tuple fields.**/
	public static Object[] toArray(Tuple t) {
		if (t instanceof ArrayTuple) {return ((ArrayTuple) t).getValues();}
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
	public static final Object namedDereference(String name, PrototypedTuple source) {
		int idx = source.prototype().indexOf(name);
		if (idx >=0) {return source.get(idx);}
		throw new InvalidNameException(name, source.prototype());
	}	
	
	/**Generic pair-wise value equality.
	 * Does NOT check names/prototypes or other supplemental information.
	 ***/
	public static boolean equals(Tuple t1, Object other) {
		if (!(other instanceof Tuple)) {return false;}
		Tuple t2 = (Tuple) other;
		
		if (t1.size() != t2.size()) {return false;}
		
		for (int i=0; i< t1.size(); i++) {
			if (t1.get(i) == null && t2.get(i) != null
					|| !t1.get(i).equals(t2.get(i))) {return false;}
		}
		
		return true;
	}


	
	/**Calculates a hash code based on the field has codes (not based on any supplemental properties).
	 * This hashCode method is consistent with Tuples.equals(tuple, object).
	 */
	public static int hashCode(Tuple t) {
		int hashCode = 1;
		for (int i=0; i<t.size(); i++) {
			int baseCode = t.get(i).hashCode();
			hashCode = hashCode * baseCode << i;	//TODO: Is this bit-shift w/wrap?
		}
		return hashCode;
	}
	
	/**Removes all fields from a tuple that are not in the prototype.*/
	public static PrototypedTuple reduce(TuplePrototype proto, PrototypedTuple t) {
		List<String> remove = new ArrayList();
		for (TupleFieldDef def: ((Iterable<TupleFieldDef>) t.prototype())) {
			String name = def.name();
			if (!proto.contains(name)) {remove.add(name);}
		}
		return delete(t, remove.toArray(new String[remove.size()]));
	}
	
	
	public static PrototypedTuple delete(PrototypedTuple t, String... fields) {
		final int[] idxs = new int[fields.length];
		for (int i=0; i< idxs.length; i++) {
			idxs[i] = t.prototype().indexOf(fields[i]);
			assert idxs[i] >=0 : "Attempt to delete element " + fields[i] + " not present in the tuple.";
		}
		return delete(t, idxs);
	}
	
	public static PrototypedTuple delete(PrototypedTuple t, int... fields) {return (PrototypedTuple) delete((Tuple) t, fields);}
	
	/**Delete fields from a tuple (returned as a new tuple).
	 * If the tuple had a prototype, the return result will too.
	 * 
	 * Behavior is undefined if the same index is listed multiple times,
	 * it is expected that the idx list only has unique values before this method is called.
	 */
	public static Tuple delete(Tuple t, int... idxs) {
		TuplePrototype proto = null;
		List<TupleFieldDef> defs = null;
		Arrays.sort(idxs);
		
		List values = new ArrayList(t.size()-idxs.length);
		
		if (t instanceof PrototypedTuple) {
			proto = ((PrototypedTuple) t).prototype();
			defs = new ArrayList(t.size()-idxs.length);
		}

		int indexAt =0;
		for (int i=0 ;i<t.size(); i++) {
			if (indexAt < idxs.length && idxs[indexAt] == i) {indexAt++; continue;}
			values.add(t.get(i));
			if (proto != null) {defs.add(proto.get(i));}
		}
		
		if (proto != null) {
			TuplePrototype p = new TuplePrototype(defs.toArray(new TupleFieldDef[defs.size()]));
			return new PrototypedArrayTuple(p, values);}
		else {return new ArrayTuple(values.toArray());}
	}
}
