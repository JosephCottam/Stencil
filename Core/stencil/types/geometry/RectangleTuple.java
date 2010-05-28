package stencil.types.geometry;

import java.awt.geom.Rectangle2D;

import stencil.tuple.AbstractTuple;
import stencil.tuple.TupleBoundsException;

public final class RectangleTuple extends AbstractTuple {
	private static final String[] FIELDS = new String[]{"X","Y","W","H"};
	private static final Number DEFAULT = new Double(0);
	private final Rectangle2D content;
	
	public RectangleTuple(Rectangle2D content) {
		super(FIELDS, DEFAULT);
		this.content = content;
	}
	
	public Object get(int idx) throws TupleBoundsException {
		if (idx ==0) {return content.getX();}
		if (idx ==1) {return content.getY();}
		if (idx ==2) {return content.getWidth();}
		if (idx ==3) {return content.getHeight();}
		throw new TupleBoundsException(idx, this);
	}

}
