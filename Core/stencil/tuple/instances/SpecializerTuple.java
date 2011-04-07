package stencil.tuple.instances;

import stencil.interpreter.tree.Specializer;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.collections.ArrayUtil;

/**Tuple created by wrapping a specializer.
 * Supports named or positional de-referencing.
 */
public final class SpecializerTuple implements Tuple {
	private final Specializer spec;
	private final TuplePrototype prototype;
	
	public SpecializerTuple(Specializer spec) {
		this.spec = spec;
		this.prototype = new SimplePrototype(ArrayUtil.fromIterator(spec.keySet(), new String[spec.size()]));
	}

	@Override
	public Object get(String name) throws InvalidNameException {return spec.get(name);}

	@Override
	public Object get(int idx) throws TupleBoundsException {
		int i =0;
		for (String key: spec.keySet()) {
			if (i==idx) {return spec.get(key);}
		}
		throw new TupleBoundsException(idx, size());
	}
	
	@Override
	public TuplePrototype getPrototype() {return prototype;}
	@Override
	public boolean isDefault(String name, Object value) {return false;}

	@Override
	public int size() {return spec.size();}
}
