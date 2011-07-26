package stencil.tuple.instances;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;

/**Tuple created by wrapping an array.
 * Does not support named de-referencing, has default values and the prototype is optional.
 * 
 * This lightweight object can be used when all prototype an default related
 * operations are resolved at compile-time.
 */
public class ArrayTuple implements Tuple {
	private final Object[] values;
	
	public ArrayTuple(Object[] values) {
		if (values == null) {this.values = new Object[0];}
		else {this.values = values;}
	}
	
	public ArrayTuple(Object v) {
		if (v.getClass().isArray()) {
			values = (Object[]) v;
		} else {	
			this.values = new Object[]{v};
		}
	}

	public Object get(int idx) throws TupleBoundsException {return values[idx];}
	public int size() {return values.length;}
	
	public String toString() {return Tuples.toString(this);}
	
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
	
	public static final ArrayTuple from(Object... values) {return new ArrayTuple(values);}
}
