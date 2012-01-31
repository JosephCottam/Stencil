package stencil.types.geometry;

import java.awt.geom.Rectangle2D;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

/**Wrapper for a rectangle.
 * These tuples are based in a Y-Up/positive coordinate system.
 * This means that the X/Y position are the lower-left of the rectangle (not the upper-left, as the standard Java2D rectangles are).
 * Translation occurs when the RectangleTuple is instantiated, so the construction arguments should not be translated.
 * Furthermore, the rectangle returned from the basis method is in the standard Y-Up/negative system.
 * @author jcottam
 *
 */
public final class RectangleTuple implements PrototypedTuple {
	public static final TuplePrototype PROTO = new TuplePrototype(new String[]{"X","Y","W","H"}, new Class[]{double.class, double.class, double.class, double.class});
	private final Rectangle2D basis;
	
	public RectangleTuple(Rectangle2D basis) {this.basis = basis;}
	
	public Object get(int idx) throws TupleBoundsException {
		if (idx ==0) {return basis.getMinX();}
		if (idx ==1) {return -basis.getMinY();}
		if (idx ==2) {return basis.getWidth();}
		if (idx ==3) {return basis.getHeight();}
		throw new TupleBoundsException(idx, this);
	}

	public String toString() {return Tuples.toString("Rectangle", this, 0);}

	@Override
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	@Override
	public TuplePrototype prototype() {return PROTO;}

	@Override
	public int size() {return PROTO.size();}
	
	public Rectangle2D basis() {return basis;}
}
