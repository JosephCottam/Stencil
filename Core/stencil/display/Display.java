package stencil.display;


/**Canonical location to store the view and canvas while a Stencil program is running
 * HACK: This will break all kinds of things if more than one view/canvas is active at a time OR if multiple interpreters are active.
 * NOTE: This is no more broken than it was when view/canvas instances were embedded in the tree node classes
 * @author jcottam
 *
 */
public abstract class Display {
	public static CanvasTuple canvas;
	public static ViewTuple view;
}
