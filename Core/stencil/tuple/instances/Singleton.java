package stencil.tuple.instances;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

/**Tuple created by wrapping a single value and giving it a name.
 * This lightweight object can be used when all prototype an default related
 * operations are resolved at compile-time.
 */
public class Singleton {
	protected  Singleton() {}
	
	public static Tuple from(Object v) {return new Value(v);}
	public static PrototypedTuple from(String name, Object v) {return new Prototyped(name, v);}
	
	private static class Value extends Singleton implements Tuple {
		protected final Object value;
		
		public Value(Object v) {this.value = v;}
	
		@Override
		public Object get(int idx) throws TupleBoundsException {
			if (idx >0) {throw new TupleBoundsException(idx, this);}
			return value;
		}
		
		@Override
		public int size() {return 1;}
		
		@Override
		public String toString() {return Tuples.toString(this);}
		
		@Override
		public boolean equals(Object other) {return Tuples.equals(this, other);}
		@Override
		public int hashCode() {return Tuples.hashCode(this);}
	}
	
	
	private static final class Prototyped extends Value implements PrototypedTuple {
		private final String name;
		
		public Prototyped(String name, Object v) {
			super(v);
			this.name = name;
		}
					
		@Override
		public TuplePrototype prototype() {return new TuplePrototype(name);}
	
		@Override
		public Object get(String name) throws InvalidNameException {
			if (name.equals(name)) {return value;}
			throw new InvalidNameException(name, this.prototype());
		}
		
		@Override
		public int size() {return 1;}
		
		@Override
		public boolean equals(Object other) {return Tuples.equals(this, other);}
		@Override
		public int hashCode() {return Tuples.hashCode(this);}
	}
}
