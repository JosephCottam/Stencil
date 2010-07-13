package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public final class Doubles implements Tuple {
	private final double[] values;

	public Doubles(double[] values) {
		this.values = values;
	}
	
	public Double get(int idx) {return values[idx];}
	public boolean isDefault(String name, Object value) {return false;}
	public int size() {return values.length;}

	public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
	public TuplePrototype getPrototype() {throw new UnsupportedOperationException();}
}