package stencil.module.operator.util;

import stencil.module.operator.StencilOperator;

/**Utility object to cache viewpoints with an associated StateID.
 * If used properly, reduces the number of viewpoints that actually need to be produced (and thus the time in the epoch lock).
 * Can increase memory usage between renderings as viewpoints are now retained indefinitely (not just for rendering).
 * Relies on the assumption that viewpoints ARE NOT mutated after they are created (they're not supposed to be...).
 */
public final class ViewpointCache {
	private static final Object[] EMPTY_ARGS= new Object[0];
	
	private int cachedID;
	private Invokeable IDFacet;
	private StencilOperator op;
	private StencilOperator cached;
	
	public ViewpointCache(StencilOperator<StencilOperator> op) {
		this.op = op;
		IDFacet = op.getFacet(StencilOperator.STATE_ID_FACET);
		cachedID = (Integer) IDFacet.invoke(EMPTY_ARGS);
		cached = op.viewpoint();
	}
		
	public StencilOperator viewpoint() {
		int newID = (Integer) IDFacet.invoke(EMPTY_ARGS);
		if (newID != cachedID) {
			cached = op.viewpoint();
			cachedID= newID;
		}
		return cached;
	}
}
