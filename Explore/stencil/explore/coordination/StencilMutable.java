package stencil.explore.coordination;

import java.util.Collection;

import stencil.explore.model.AdapterOpts;
import stencil.explore.model.sources.StreamSource;

/**Provide proper methods for mutating a Stencil.
 *
 * When providing mutators, the implementing class may raise events
 * to inform other classes of the changes.  To prevent endless loops
 * of events, an event should only be raised if the state of the implementing
 * class changed (e.g. if setting the stencil to the current stencil, do not raise an event).
 */
public interface StencilMutable {
	public static interface Config extends StencilMutable {
		public void setAdapterOpts(AdapterOpts opts);
	}

	public static interface Stencil extends StencilMutable {
		public void setStencil(String stencil);
	}

	public static interface Sources<T extends StreamSource> extends StencilMutable {
		public void setSources(Collection<? extends T> source);
	}
}
