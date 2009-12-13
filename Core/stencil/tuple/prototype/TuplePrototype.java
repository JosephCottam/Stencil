package stencil.tuple.prototype;

public interface TuplePrototype<T extends TupleFieldDef> extends Iterable<T> {
	public TupleFieldDef get(int idx);
	public boolean contains(String name);
	public int size();
	public int indexOf(String name);
}