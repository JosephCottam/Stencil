package stencil.types.geometry;

import java.awt.geom.Point2D;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;


/**Representation of a point.
 * The basis will be in Y-Up/Negative coordinates but getting X or y will be in Y-Up/positive coordinates.
 **/
public final class PointTuple implements PrototypedTuple {
	private static final TuplePrototype PROTO = new TuplePrototype(new String[]{"x", "y"}, new Class[]{double.class, double.class});
	
	private final Point2D p;
	
	public PointTuple(Point2D p) {this.p = p;}
	
	/**Assumes y is in Y-Up/Positive coordinates.**/
	public PointTuple(double x, double y) {this.p = new Point2D.Double(x,-y);}
	
	public Object get(int idx) throws TupleBoundsException {
		if (idx ==0) {return p.getX();}
		if (idx ==1) {return -p.getY();}
		throw new TupleBoundsException(idx, this);
	}
	
	public String toString() {return Tuples.toString("Point", this, 0);}

	@Override
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	@Override
	public TuplePrototype prototype() {return PROTO;}

	@Override
	public int size() {return PROTO.size();}
	
	public Point2D basis() {return p;}
	
	public double x() {return p.getX();}
	public double y() {return -p.getY();}
}
