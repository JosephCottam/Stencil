package stencil.module.operator.util;

/**Utility object to cache viewpoints with an associated StateID.
 * If used properly, reduces the number of viewpoints that actually need to be produced (and thus the time in the epoch lock).
 * Can increase memory usage between renderings as viewpoints are now retained indefinitely (not just for rendering).
 * Relies on the assumption that viewpoints ARE NOT mutated after they are created (they're not supposed to be...).
 */
public final class ViewpointCache<T> {
	private int cachedID;
	private T cached;

	public boolean useCached(int stateID) {return  cachedID == stateID && cached != null;}
	public T cached() {return cached;}
	public void cache(T viewpoint) {this.cached = viewpoint;}
}
