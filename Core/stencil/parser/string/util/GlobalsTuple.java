package stencil.parser.string.util;

import java.util.List;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.parser.tree.*;
import stencil.tuple.prototype.SimplePrototype;

/**Tuple that encapsulates the globals of a program.*/
public class GlobalsTuple implements Tuple {
	private final Atom[] values;
	private final TuplePrototype prototype;
	
	public GlobalsTuple(List<Const> defs) {
		String[] names = new String[defs.size()];
		values = new Atom[defs.size()];
		for (int i =0; i<values.length; i++) {
			values[i] = defs.get(i).getValue();
			names[i] = defs.get(i).getName();
		}
		prototype = new SimplePrototype(names);
	}
	
	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}

	public Object get(int idx) throws TupleBoundsException {
		try {
			return values[idx];
		} catch (ArrayIndexOutOfBoundsException e) {throw new TupleBoundsException(idx, this);}
	}

	public TuplePrototype getPrototype() {return prototype;}

	public boolean isDefault(String name, Object value) {return false;}

	public int size() {return values.length;}

}
