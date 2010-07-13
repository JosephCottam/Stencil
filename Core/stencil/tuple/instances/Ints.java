package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public final class Ints implements Tuple {
	private final int[] values;

	public Ints(int[] values) {
		this.values = values;
	}
	
	public Integer get(int idx) {return values[idx];}
	public boolean isDefault(String name, Object value) {return false;}
	public int size() {return values.length;}

	public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
	public TuplePrototype getPrototype() {throw new UnsupportedOperationException();}
}