package stencil.display;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;

import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.TuplePrototype;

/** The ViewTuple is the interface to a view
 * of the canvas.  To be complete, an adapter
 * must provide appropriate view tuples to manipulate
 * their camera-like objects and transition between
 * application/screen, canvas and view coordinate spaces.
 *
 * ViewTuples do not set the width/height/X/Y of the panel,
 * rather they manipulate the zoom or pan levels displayed
 * in that panel.
 *
 * Due to graphics system constraints, the width and height may not
 * be independently set-able. For adapters where this is not
 * possible, it is suggested that ZOOM be supported instead
 * and WIDTH/HEIGHT throw exceptions.  Systems capable of setting
 * independent X and Y zoom distortions may handle ZOOM as they see
 * fit. See the adapter implementation for details of its implementation.
 *
 * View tuple also provide basic abilities to convert between
 * the view and the canvas coordinate system.
 *
 * @author jcottam
 *
 */
public abstract class ViewTuple implements PrototypedTuple {
	public static final String VIEW_IMPLANTATION = "STENCIL_VIEW";
	public static final TuplePrototype PROTOTYPE;
	public static final String PROTOTYPE_STRING = "(double ZOOM, double X, double Y, double W, double H, double PORTAL_WIDTH, double PORTAL_HEIGHT)";
	
	static {
		TuplePrototype proto = null;
		try {proto = ParseStencil.prototype(PROTOTYPE_STRING, false);}
		catch (ProgramParseException e) {System.err.println("Error parsing view tuple prototype.)");}
		PROTOTYPE = proto;
	}

	//TODO: Convert so the numeric de-reference is the primary one
	public Object get(int idx) {
		try {return get(PROTOTYPE.get(idx).name());}
		catch (IndexOutOfBoundsException e) {throw new TupleBoundsException(idx, size());}
	}

	public TuplePrototype prototype() {return PROTOTYPE;}
	public int size() {return PROTOTYPE.size();}		
	/**Gets the default value for the named property.
	 * If the named property has no defined default, it is assumed to be 'null'.
	 *
	 * @param name Property to look up default value of.
	 * @return Default value of property.
	 */
	public boolean isDefault(String name, Object value) {return false;}

	public String toString() {return stencil.tuple.Tuples.toString(this);}

	/**Given a point in the canvas, where is it in the view?
	 * This method may return negative values in the points, indicating
	 * that the source point is not actually in the view right now.
	 */
	public abstract Point2D canvasToView(Point2D p);

	/**Given a point in the view, what is the corresponding canvas coordinate?*/
	public abstract Point2D viewToCanvas(Point2D p);

	/**Given a distance in the view, what is the corresponding distance in the canvas?*/
	public abstract Dimension2D viewToCanvas(Dimension2D p);

	/**Given a distance in the canvas, what is the corresponding distance in the view?*/
	public abstract Dimension2D canvasToView(Dimension2D p);
	
	/**What is the current view transform?*/
	public abstract AffineTransform viewTransform();

}
