package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

public final class NumericSingleton extends Number implements Tuple {
	private final double dv;
	private final long lv;
	private final boolean preferLong;
	
	public NumericSingleton(float f) {this((double) f);}
	public NumericSingleton(int i) {this((long) i);}

	public NumericSingleton(long l) {
		dv = (double) l;
		lv = l;
		preferLong = true;
	}
	
	public NumericSingleton(double d) {
		dv = d;
		lv = (long) d;
		preferLong = false;
	}
	
	public NumericSingleton(Number n) {
		dv = n.doubleValue();
		lv = n.longValue();
		preferLong = (n instanceof Long || n instanceof Integer);
	}

	public double doubleValue() {return dv;}
	public float floatValue() {return (float) dv;}
	public int intValue() {return (int) lv;}
	public long longValue() {return lv;}

	public Object get(int idx) throws TupleBoundsException {
		if (idx >0) {throw new TupleBoundsException(idx, 1);}
		if (preferLong) {return lv;}
		return dv;
	}

	public TuplePrototype getPrototype() {return new stencil.tuple.prototype.SimplePrototype(Tuple.DEFAULT_KEY);}
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}


	public boolean isDefault(String name, Object value) {
		if (value instanceof Number) {return ((Number) value).doubleValue() == 0;}
		return false;
	}

	public int size() {return 1;}	
}