package stencil.interpreter;

public interface Viewpoint<C> {
	/**A viewpoint is how the entity as it current exists.
	 * Viewpoints are NOT allowed to reflect future updates to the source operator.
	 * They are NOT allowed to modify state themselves.  
	 */
	public C viewpoint();
}
