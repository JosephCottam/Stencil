package stencil.explore.coordination;

import java.util.List;

import stencil.explore.model.AdapterOpts;
import stencil.explore.model.sources.StreamSource;


/**Event indicating that a stencil object was changed was changed.*/
public class StencilEvent<T> extends java.util.EventObject {
	public static enum Type {Stencil, Sources, Config, All}

	/**Event to indicate the configuration changed.
	 * This includes compiler/interpreter configuration or application information.
	 */
	public static class ConfigChanged extends StencilEvent<AdapterOpts> {
		public ConfigChanged(Object source, AdapterOpts opts) {super(source, Type.Config, opts);}
	}

	/**Event to indicate the actual stencil code changed.*/
	public static class StencilChanged extends StencilEvent<String> {
		public StencilChanged(Object source, String value) {super(source, Type.Stencil, value);}
	}

	/**Event to indicate that the stencil sources have changed.
	 * This can indicate that a source has been either added, removed or edited.
	 */
	public static class SourcesChanged extends StencilEvent<List<StreamSource>> {
		public SourcesChanged(Object source, List<StreamSource> streamSource) {super(source, Type.Sources, streamSource);}
	}

	protected Type type;
	protected T value;

	public StencilEvent(Object source, Type type) {this(source, type, null);}
	public StencilEvent(Object source, Type type, T value) {
		super(source);
		this.type = type;
		this.value = value;
	}

	public T getValue() {return value;}
}
