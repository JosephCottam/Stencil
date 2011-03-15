package stencil.interpreter;

/**A viewpoint is how the entity as it current exists.
 * Viewpoints are NOT allowed to reflect future updates to the source operator.
 * They are NOT allowed to modify state themselves.
 * This does not imply that this viewpoint is independent of any prior viewpoints.
 * 
 * Viewpoints produced MAY be type compatible with their parents, but they are
 * not required to be.
 */
public interface Viewpoint<C> {
	/**Return a viewpoint.
	 * 
	 * The viewpoint may be calculated at this time, or it may be prior calculated
	 * value returned (possibly multiple times).
	 * 
	 * **/
	public C viewpoint();
}
