package stencil.tuple;

public interface Tuple {
	public static final String DEFAULT_KEY = "VALUE";

	/**Get an item by index from a tuple.*/
	public abstract Object get(int idx) throws TupleBoundsException;

	public abstract int size();
}