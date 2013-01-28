package stencil;


/**Tagging interface that indicates that objects accessed through
 * classes implementing this method are "stable" with respect to rendering.
 * The meaning of "stable" depends on the adaptor, but "immutable" is a fair default assumption.
 **/
public interface RenderableView { }
