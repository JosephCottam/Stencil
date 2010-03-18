package stencil.tuple;

import stencil.tuple.prototype.TuplePrototype;

public final class NumericSingleton extends Number implements Tuple {
	private final double dv;
	private final long lv;
	
	public NumericSingleton(Number n) {
		dv = n.doubleValue();
		lv = n.longValue();
	}

	public double doubleValue() {return dv;}
	public float floatValue() {return (float) dv;}
	public int intValue() {return (int) lv;}
	public long longValue() {return lv;}

	public Object get(int idx) throws TupleBoundsException {
		if (idx >0) {throw new TupleBoundsException(idx, 1);}
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
