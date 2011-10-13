package stencil.interpreter.tree;

import java.util.Arrays;

import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototypes;

public final class Specializer extends PrototypedArrayTuple {
	public Specializer(String[] keys, Object[] vals) {super(keys, vals);}
	
	public boolean containsKey(String key) {return prototype().contains(key);}
	public Iterable<String> keySet() {return Arrays.asList(TuplePrototypes.getNames(prototype));}
	
	public String toString() {return "Specializer -- " + Tuples.toString(this);}

	public Object get(String key, Object defVal) {return this.containsKey(key) ? get(key) : defVal;}
}
