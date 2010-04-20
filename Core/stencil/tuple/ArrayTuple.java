package stencil.tuple;

import java.lang.reflect.Array;

import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

/**Tuple created by wrapping an array.
 * Does not support named de-referencing, has default values and the prototype is optional.
 * 
 * This lightweight object can be used when all prototype an default related
 * operations are resolved at compile-time.
 */
public final class ArrayTuple implements Tuple {
	private static final String PREFIX = "***";
	private final Object values;
		
	public ArrayTuple(Object... values) {this.values = values;}
	
	/**Take the passed 'values' object and verify that it is an array.
	 * 
	 * HACK: The certifyArray is a potentially BAD IDEA.  It ONLY exists
	 * so this constructor is not confused with the var-args constructor
	 * when an array tuple wiht a single value is needed.
	 * 
	 * @param values An array instance (primitive or Object)
	 * @param certifyArray Ignored, used only to distinguish this constructor from others
	 */
	public ArrayTuple(Object values, boolean certifyArray) {
		if (!values.getClass().isArray()) {throw new IllegalArgumentException("Can only work with arrays");}
		this.values = values;
	}
	
	public TuplePrototype getPrototype() {
		throw new UnsupportedOperationException("Prototype must be supplied for this operation to be supported.");
	}

	
	public Object get(int idx) throws TupleBoundsException {return Array.get(values, idx);}
	public Object get(String name) throws InvalidNameException {
		int idx=0;
		if (!name.equals(PREFIX)) {idx = Integer.parseInt(name.substring(PREFIX.length()));}
		return get(idx);
	}
	public boolean isDefault(String name, Object value) {return false;}
	public int size() {return Array.getLength(values);}
	
	/**Get the values array backing this tuple.  This is a reference to 
	 * the internal state of the tuple, so modifications to the array are
	 * reflected in the tuple.
	 */
	public Object getValues() {return values;}
	public String toString() {return Tuples.toString(this, TuplePrototypes.defaultNames(size(), PREFIX));}
}
