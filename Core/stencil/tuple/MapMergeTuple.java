package stencil.tuple;

/**
 * Tuple representing the result of merging a map split in
 * an operator chain.  This should ONLY be used by the interpreter.
 */
public class MapMergeTuple extends ArrayTuple {
	public MapMergeTuple(Object... values) {super(values);}
}
