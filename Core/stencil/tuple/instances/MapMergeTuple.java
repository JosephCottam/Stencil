package stencil.tuple.instances;

import stencil.tuple.Tuple;

/**
 * Tuple representing the result of merging a map split in
 * an operator chain.  This should ONLY be used by the interpreter.
 */
public class MapMergeTuple extends ArrayTuple {
	public MapMergeTuple(Tuple... values) {super((Object[]) values);}
	
	public Tuple getTuple(int i) {return (Tuple) super.get(i);}
}
