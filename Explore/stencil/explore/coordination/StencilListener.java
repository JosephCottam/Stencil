package stencil.explore.coordination;

import java.util.EventListener;

/**Listen to events published relative to Stencils.
 *
 * As a general rule, if the event consumer is invoking StencilMutable objects,
 * it should invoke the methods with the 'raise' flag set to false.
 */

public abstract class StencilListener {
	public interface ConfigChanged extends EventListener {
		public void configChanged(StencilEvent.ConfigChanged configUpdate);
	}

	public interface StencilChanged extends EventListener {
		public void stencilChanged(StencilEvent.StencilChanged stencilUpdate);
	}

	public interface SourcesChanged extends EventListener {
		public void sourceChanged(StencilEvent.SourcesChanged sourceUpdate);
	}
}
