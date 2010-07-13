package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

/**Tuple created by wrapping an array.
 * Does not support named de-referencing, has default values and the prototype is optional.
 * 
 * This lightweight object can be used when all prototype an default related
 * operations are resolved at compile-time.
 */
public class Singleton implements Tuple {
	private final Object value;
	
	public Singleton(Object v) {this.value = v;}
				
	public TuplePrototype getPrototype() {
		throw new UnsupportedOperationException();
	}

	public Object get(int idx) throws TupleBoundsException {
		if (idx >0) {throw new TupleBoundsException(idx, this);}
		return value;
	}
	
	public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
	public boolean isDefault(String name, Object value) {return false;}
	public int size() {return 1;}
	
	public String toString() {return Tuples.toString(this);}
}
