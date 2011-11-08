package stencil.types.gradient;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.collections.ArrayUtil;

/**Implements paint only to allow it to be used in the Stroked implementation
 * that expects a paint based on PEN_COLOR.  THIS SHOULD BE FIXED SOMEHOW....ANY
 * PAINT MEHTODS WILL THROW AN EXCEPTION!!!!
 * 
 * @author jcottam
 *
 */
public class GradientTuple implements PrototypedTuple, Paint {
	private static String SELF_LABEL = "self";
	private static String START_LABEL = "start";
	private static String END_LABEL = "end";
	private static String LENGTH_LABEL = "length";
	private static String ABSOLUTE_LABEL = "absolute";
	private static String CYCLIC_LABEL = "cyclic";
	
	private static final String[] FIELDS = new String[]{SELF_LABEL, START_LABEL, END_LABEL, LENGTH_LABEL, ABSOLUTE_LABEL, CYCLIC_LABEL};
	private static final Class[] TYPES = new Class[]{GradientTuple.class, Color.class, Color.class, double.class, boolean.class, boolean.class};
	private static final TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS, TYPES);
	public static final int SELF = ArrayUtil.indexOf(SELF_LABEL, FIELDS);
	public static final int START = ArrayUtil.indexOf(START_LABEL, FIELDS);
	public static final int END = ArrayUtil.indexOf(END_LABEL, FIELDS);
	public static final int LENGTH = ArrayUtil.indexOf(LENGTH_LABEL, FIELDS);
	public static final int ABSOLUTE = ArrayUtil.indexOf(ABSOLUTE_LABEL, FIELDS);
	public static final int CYCLIC = ArrayUtil.indexOf(CYCLIC_LABEL, FIELDS);

	
	private final Color one;
	private final Color two;
	private final double length;
	private final boolean absolute;
	private final boolean cyclic;
	
	
	public GradientTuple(Color one, Color two, double length, boolean absolute, boolean cyclic) {
		this.one = one;
		this.two = two;
		this.length = length;
		this.absolute = absolute;
		this.cyclic = cyclic;
	}
	
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}

	public Object get(int idx) throws TupleBoundsException {
		if (idx==SELF) {return this;}
		if (idx==START) {return one;}
		if (idx==END) {return two;}
		if (idx==LENGTH) {return length;}
		if (idx==ABSOLUTE) {return absolute;}
		if (idx==CYCLIC) {return cyclic;}
		throw new TupleBoundsException(idx,size());
	}

	@Override
	public TuplePrototype prototype() {return PROTOTYPE;}

	public boolean isDefault(String name, Object value) {return false;}

	public int size() {return PROTOTYPE.size();}
	
	public GradientPaint getPaint(Point2D start, Point2D end) {	
		double p;
		if (length >0 && absolute) {
			p = length/start.distance(end);			
		} else if (length > 0) {
			p = length * start.distance(end);
		} else {
			p =1;
		}
		
		double x = (end.getX() - start.getX()) * p + start.getX();
		double y = (end.getY() - start.getY()) * p + start.getY();
		end = new Point2D.Double(x,y);			
		
		return new GradientPaint(start, one, end, two, cyclic);
	}


	public int getTransparency() {return Paint.TRANSLUCENT;}
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		Paint p = getPaint(new Point2D.Double(userBounds.getMinX(), userBounds.getMinY()), new Point2D.Double(userBounds.getMaxX(), userBounds.getMaxY()));
		return p.createContext(cm, deviceBounds, userBounds, xform, hints);
	}
}
