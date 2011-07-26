package stencil.display;

import java.awt.Rectangle;

import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
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
	public static final String PROTOTYPE_STRING ="(color BACKGROUND_COLOR, double X, double Y, double WIDTH, double HEIGHT, double RIGHT, double BOTTOM)";
	public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
	
	static {
		TuplePrototype proto = null;
		try {proto = ParseStencil.prototype(PROTOTYPE_STRING, false);}
		catch (ProgramParseException e) {System.err.println("Error parsing view tuple prototype.)");}
		PROTOTYPE = proto;
	}


	/**Return the actual backing component*/
	public abstract DisplayCanvas getComponent();
	protected abstract Rectangle getBounds();

	public Object get(int idx) {
		try {return get(PROTOTYPE.get(idx).name());} 
		catch (IndexOutOfBoundsException e) {throw new TupleBoundsException(idx, size());}
	}

	public Object get(String name) throws InvalidNameException {
		Rectangle bounds = getBounds();

		if (BACKGROUND_COLOR.equals(name)) return getComponent().getBackground();
		if ("X".equals(name)) return bounds.getX();
		if ("Y".equals(name)) return bounds.getY();
		if ("WIDTH".equals(name)) return bounds.getWidth();
		if ("HEIGHT".equals(name)) return bounds.getHeight();
		if (name.equals("RIGHT")) {return bounds.getX() + bounds.getWidth();}
		if (name.equals("BOTTOM")) {return (-bounds.getY()) + bounds.getHeight();}
		
		throw new InvalidNameException(name, PROTOTYPE);
	}

	public double getX() {return getBounds().getX();}
	public double getY() {return getBounds().getY();}
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
