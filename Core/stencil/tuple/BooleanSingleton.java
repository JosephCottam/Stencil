package stencil.tuple;

import stencil.tuple.prototype.TuplePrototype;

public final class BooleanSingleton implements Tuple {
	public static final BooleanSingleton TRUE = new BooleanSingleton(true); 
	public static final BooleanSingleton FALSE = new BooleanSingleton(false); 

	private final boolean v;
	
	public BooleanSingleton(boolean v) {this.v = v;}
	public boolean value() {return v;}

	public Object get(int idx) throws TupleBoundsException {
		if (idx >0) {throw new TupleBoundsException(idx, 1);}
		return v;
	}

	public TuplePrototype getPrototype() {return new stencil.tuple.prototype.SimplePrototype(Tuple.DEFAULT_KEY);}
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	public boolean isDefault(String name, Object value) {return value.equals(false);}

	public int size() {return 1;}
	
	public static BooleanSingleton instance(boolean v) {
		if (v) {return TRUE;}
		else {return FALSE;}
	}	
}
