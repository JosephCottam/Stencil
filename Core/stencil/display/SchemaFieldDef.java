package stencil.display;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimpleFieldDef;
import stencil.tuple.prototype.TuplePrototype;


/**Field definition class used to define table schemas.  
 * Adds the ability to flag fields as constant.**/
public class SchemaFieldDef<T> extends SimpleFieldDef<T> {
	/**Is this field filled with all single values?*/
	private final boolean constant;
		
	/**Infer the type from defVal and flag as non-constant.**/
	public SchemaFieldDef(String name, T defVal) {
		this(name, defVal, (Class<T>) defVal.getClass(), false);
	}

	/**Specify type of the column independent of the default value.**/
	public SchemaFieldDef(String name, T defVal, Class<T> type) {
		this(name, defVal, type, false);
	}

	
	/**Set all parameters explicitly.  defValue must be assignable from the type.**/
	public SchemaFieldDef(String name, T defVal, Class<T> type, boolean constant) {
		super(name, type, defVal);
		this.constant = constant;
	}
	
	public boolean isConstant() {return constant;}
	public String toString() {
		String prefix;
		if (isConstant()) {
			prefix = "CONST - ";
		} else {
			prefix = "";
		}
		return String.format("%1$s %2$s : %3$s", prefix, super.toString(), defaultValue());
	}	
	
	public SchemaFieldDef rename(String newName) {
		return new SchemaFieldDef(newName, defaultValue(), type(), constant);
	}

	
	
	public static final PrototypedTuple asTuple(TuplePrototype<SchemaFieldDef> defs) {return new TupleWrapper(defs);}
	
	/**Because the SchemaFieldDef carries defaults,
	 * there is sufficient information to treat a TuplePrototyped constituted
	 * of SchemaFieldDefs as a tuple.  This class achieves that effect.
	 */
	private static final class TupleWrapper implements PrototypedTuple {
		private final TuplePrototype<SchemaFieldDef> prototype; 
		public TupleWrapper(TuplePrototype<SchemaFieldDef> prototype) {
			this.prototype = prototype;
		}
		
		@Override
		public Object get(String name) throws InvalidNameException {
			return get(prototype.indexOf(name));
		}

		@Override
		public Object get(int idx) throws TupleBoundsException {
			return prototype.get(idx).defaultValue();
		}

		@Override
		public TuplePrototype prototype() {return prototype;}

		@Override
		public int size() {return prototype.size();}
		
		public String toString() {return Tuples.toString(this);}
	}
}
