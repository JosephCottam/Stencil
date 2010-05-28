package stencil.tuple;

import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

public abstract class AbstractTuple implements Tuple {
	protected final Object defaultValue;
	protected final String[] fields;
	
	
	protected AbstractTuple(String[] fields, Object defaultValue) {
		this.fields = fields;
		this.defaultValue = defaultValue;
	}
	
	public boolean isDefault(String name, Object value) {return defaultValue.equals(value);}

	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}
	
	public int size() {return fields.length;}
	public String toString() {return Tuples.toString(this);}
	public TuplePrototype getPrototype() {return new SimplePrototype(fields);}
}
