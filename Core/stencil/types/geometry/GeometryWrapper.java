package stencil.types.geometry;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;

/**Wrap the basic geometric units in tuples.  
 * Can handle: Point2D and Rectangle2D
 */

public class GeometryWrapper implements TypeWrapper {
	private static Class[] ACCEPTS = {Point2D.Double.class, Rectangle2D.Double.class, Point2D.Float.class, Rectangle2D.Float.class, Point.class, Rectangle.class};
	
	public Class[] appliesTo() {return ACCEPTS;}

	public Object convert(Object v, Class c) {
		if (v.getClass().isArray()) {
			int length = Array.getLength(v);
			if (c.equals(Rectangle2D.class) && length == 4) {
				double x = Converter.toDouble(Array.get(v, 0));
				double y = Converter.toDouble(Array.get(v, 0));
				double w = Converter.toDouble(Array.get(v, 0));
				double h = Converter.toDouble(Array.get(v, 0));
				return new Rectangle2D.Double(x,y,w,h);
			} else if (c.equals(Point2D.class) && length ==2) {
				double x = Converter.toDouble(Array.get(v, 0));
				double y = Converter.toDouble(Array.get(v, 0));
				return new Point2D.Double(x,y);
			}
		}
		throw new ConversionException(v,c);
	}

	public Object external(Tuple t, TuplePrototype p, Class c) {
		if (c.isAssignableFrom(Point2D.class)) {
			int xIdx = p.indexOf("X");
			int yIdx = p.indexOf("Y");
			double x = Converter.toDouble(t.get(xIdx));
			double y = Converter.toDouble(t.get(yIdx));
			return new Point2D.Double(x,y);
		} else if (c.isAssignableFrom(Rectangle2D.class)) {
			int xIdx = p.indexOf("X");
			int yIdx = p.indexOf("Y");
			int wIdx = p.indexOf("W");
			int hIdx = p.indexOf("H");
			double x = Converter.toDouble(t.get(xIdx));
			double y = Converter.toDouble(t.get(yIdx));
			double w = Converter.toDouble(t.get(wIdx));
			double h = Converter.toDouble(t.get(hIdx));
			return new Rectangle2D.Double(x,y,w,h);
			
		}
		throw new RuntimeException("Could not externalize....");
	}

	@Override
	public Tuple toTuple(Object o) {
		if (o instanceof Point2D) {return new PointTuple((Point2D) o);}
		if (o instanceof Rectangle2D) {return new RectangleTuple((Rectangle2D) o);}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
