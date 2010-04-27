package stencil.interpreter;

import stencil.tuple.Tuple;

public interface TupleStore {
	/**Will this store accept the passed tuple?*/
	public boolean canStore(Tuple t);
	
	/**Store the tuple given. 
	 * This must succeed if canStore would return true.
	 * This can fail (should throw an exception) if canStore would return false;
	 * @param t
	 */
	public void store(Tuple t);
	
	/**Name of this storage location.*/
	public String getName();
}
