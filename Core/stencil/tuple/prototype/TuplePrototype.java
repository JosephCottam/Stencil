package stencil.tuple.prototype;

public interface TuplePrototype<T extends TupleFieldDef> {
	public TupleFieldDef get(int idx);
	
	/**Is there a field of the given name?*/
	public boolean contains(String name);
	
	/**How many fields in this prototype?*/
	public int size();
	
	/**What is the index of the given name?
	 * Negative value indicates the name is not found.
	 **/
	public int indexOf(String name);
	
	public Iterable<T> fields();
}