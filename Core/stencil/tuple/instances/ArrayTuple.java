package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

/**Tuple created by wrapping an array.
 * Does not support named de-referencing, has default values and the prototype is optional.
 * 
 * This lightweight object can be used when all prototype an default related
 * operations are resolved at compile-time.
 */
public class ArrayTuple implements Tuple {
	private static final String PREFIX = "***";
	private final Object[] values;
	
	public ArrayTuple(Object... values) {this.values = values;}
	public ArrayTuple(Object v) {
		if (v.getClass().isArray()) {
			values = (Object[]) v;
		} else {	
			this.values = new Object[]{v};
		}
	}
				
	public TuplePrototype getPrototype() {
		throw new UnsupportedOperationException("Prototype must be supplied for this operation to be supported.");
	}

	public Object get(int idx) throws TupleBoundsException {return values[idx];}
	public Object get(String name) throws InvalidNameException {
		int idx=0;
		if (!name.equals(PREFIX)) {idx = Integer.parseInt(name.substring(PREFIX.length()));}
		return get(idx);
	}
	public boolean isDefault(String name, Object value) {return false;}
	public int size() {return values.length;}
	
	public String toString() {return Tuples.toString(this, TuplePrototypes.defaultNames(size(), PREFIX));}
	
	public boolean equals(Object other) {
		if (!(other instanceof Tuple)) {return false;}
		Tuple t = (Tuple) other;
		for (int i=0; i< values.length; i++) {
			if (values[i] == null && t.get(i) != null
					|| !values[i].equals(t.get(i))) {return false;}
		}
		return true;
	}
	
	/**Direct access to the contained array, for stencil internal use only.*/
	public Object[] getValues() {return values;} 
}
