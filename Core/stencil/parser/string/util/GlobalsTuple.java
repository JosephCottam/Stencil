package stencil.parser.string.util;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.parser.tree.*;

/**Tuple that encapsulates the globals of a program.*/
public class GlobalsTuple implements PrototypedTuple {
	private final Object[] values;
	private final TuplePrototype prototype;
	
	public GlobalsTuple(StencilTree defs) {
		if (defs == null) {
			prototype = new TuplePrototype();
			values = new Object[0];
		} else {
			String[] names = new String[defs.getChildCount()];
			values = new Object[defs.getChildCount()];
			for (int i =0; i<values.length; i++) {
				values[i] = ((Const) defs.getChild(i)).getValue();
				names[i] = defs.getChild(i).getText();
			}
			prototype = new TuplePrototype(names);
		}
	}
	
	@Override
	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}

	@Override
	public Object get(int idx) throws TupleBoundsException {
		try {return values[idx];}
		catch (ArrayIndexOutOfBoundsException e) {throw new TupleBoundsException(idx, this);}
	}

	@Override
	public TuplePrototype prototype() {return prototype;}

	public boolean isDefault(String name, Object value) {return false;}

	@Override
	public int size() {return values.length;}

}
