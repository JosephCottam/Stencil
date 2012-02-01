package stencil.explore.coordination;

import stencil.explore.model.AdapterOpts;

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
