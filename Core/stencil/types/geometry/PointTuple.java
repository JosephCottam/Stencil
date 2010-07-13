package stencil.types.geometry;

import java.awt.geom.Point2D;

import stencil.tuple.AbstractTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;

public final class PointTuple extends AbstractTuple {
	private static final String[] FIELDS = new String[]{"X","Y"};
	private static final Number DEFAULT = new Double(0);
	private final Point2D content;
	
	public PointTuple(Point2D content) {
		super(FIELDS, DEFAULT);
		this.content = content;
	}
	
	public Object get(int idx) throws TupleBoundsException {
		if (idx ==0) {return content.getX();}
		if (idx ==1) {return content.getY();}
		throw new TupleBoundsException(idx, this);
	}
	
	public String toString() {return Tuples.toString("Point", this, 0);}
}
