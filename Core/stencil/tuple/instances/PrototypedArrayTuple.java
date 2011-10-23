package stencil.tuple.instances;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.TypeValidationException;
import stencil.tuple.prototype.TupleFieldDef;
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
public class PrototypedArrayTuple<T extends TupleFieldDef> implements PrototypedTuple {
	protected TuplePrototype<T> prototype;
	protected Object[] values;
	
	/**Create a new tuple.
	 *
	 * @param names Names of the values present in the tuple.
	 * @param values Values to be stored in the tuple.
	 */
	public PrototypedArrayTuple(Collection<String> names, Collection values) {this(names.toArray(new String[0]), values.toArray());}
	public PrototypedArrayTuple(String[] names, Object[] values) {this(names, TuplePrototypes.defaultTypes(names.length), values);}
	public PrototypedArrayTuple(List<String> names, List<Class> types, List<Object> values) {this(names, types, values.toArray());}
	
	public PrototypedArrayTuple(List<String> names, List<Class> types, Object[] values) {this(names.toArray(new String[0]), types.toArray(new Class[types.size()]), values);}
		public PrototypedArrayTuple(String[] names, Class[] types, Object[] values) {
		assert types != null : "Types may not be null";
		assert names != null : "Names may not be null.";
		assert names.length == values.length : "Value and name list not of the same length." + Arrays.deepToString(names) + " vs. " + Arrays.deepToString(values);
		assert findDuplicateName(names) ==  null : "Duplicate name found in names list: " + findDuplicateName(names);

		this.prototype = new TuplePrototype(names, types);
		this.values = validate(types, values);
	}
	
	public PrototypedArrayTuple(TuplePrototype prototype, List values) {
		this (prototype, values.toArray(), false);
	}

	public PrototypedArrayTuple(TuplePrototype prototype, Object[] values) {
		this (prototype, values, false);
	}
	
	public PrototypedArrayTuple(TuplePrototype prototype, Object[] values, boolean doConversions) {
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
	
	public boolean equals(Object other) {return Tuples.equals(this, other);}
	public int hashCode() {return Tuples.hashCode(this);}
	
	/**Returns a string as-per the static toString() method.**/
	public String toString() {return Tuples.toString(this);}

	public TuplePrototype<T> prototype() {return prototype;}
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}
	public Object get(int idx) {return values[idx];}
	public int size() {return values.length;}
}

