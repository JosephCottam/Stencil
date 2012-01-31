package stencil.display;

import java.awt.Rectangle;

import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

/**The CanvasTuple represents an actual drawing surface
 * to the Stencil system.  This partial implementation 
 * needs to be completed by an adapter.
 *
 * @author jcottam
 *
 */
public abstract class CanvasTuple implements PrototypedTuple {
	//Other attribute options:
	//   Imposition (POLAR, CARTESION, ELASTIC, etc)
	//   Origin Translation: 0,0 is center?  bottom left? top right?
	//   Scale directions: Does Y get positive going up or down?

	public static final String CANVAS_IMPLANTATION = "STENCIL_CANVAS";
	public static final TuplePrototype PROTOTYPE;
	public static final String PROTOTYPE_STRING ="(color COLOR, double X, double Y, double W, double H)";
	public static final String BACKGROUND_COLOR = "COLOR";
	
	private static int COLOR, X,Y,W,H;
	
	static {
		TuplePrototype proto = null;
		try {proto = ParseStencil.prototype(PROTOTYPE_STRING, false);}
		catch (ProgramParseException e) {System.err.println("Error parsing view tuple prototype.)");}
		PROTOTYPE = proto;
		
		COLOR = PROTOTYPE.indexOf("BACKGROUND_COLOR");
		X = PROTOTYPE.indexOf("X");
		Y = PROTOTYPE.indexOf("Y");
		W = PROTOTYPE.indexOf("W");
		H = PROTOTYPE.indexOf("H");
	}


	/**Return the actual backing component*/
	public abstract DisplayCanvas getComponent();
	protected abstract Rectangle getBounds();

	public Object get(int idx) {
		if (idx == COLOR) return getComponent().getBackground();
		if (idx == X) {return getX();}
		if (idx == Y) {return getY();}
		if (idx == W) {return getWidth();}
		if (idx == H) {return getHeight();}
		throw new TupleBoundsException(idx, size());
	}

	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}

	public double getX() {return getBounds().getX();}
	
	/**Return the Y-positive/Up (tuple convention) for Y position.**/
	public double getY() {return -getBounds().getY();}
	public double getWidth() {return getBounds().getWidth();}
	public double getHeight() {return getBounds().getHeight();}

	public int size() {return PROTOTYPE.size();}
	public TuplePrototype prototype() {return PROTOTYPE;}

	public String toString() {return stencil.tuple.Tuples.toString(this);}

	/**Gets the default value for the named property.
	 * If the named property has no defined default, it is assumed to be 'null'.
	 *
	 * @param name Property to look up default value of.
	 * @return Default value of property.
	 */
	public boolean isDefault(String name, Object value) {return false;}
}
