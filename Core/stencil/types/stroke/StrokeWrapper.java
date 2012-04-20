package stencil.types.stroke;

 import java.awt.BasicStroke;
import java.awt.Stroke;

import stencil.tuple.Tuple;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;

/**Pen is the pattern information about a stroke or
 * a fill, but not its color elements (those are in "Paint").*/
public class StrokeWrapper implements TypeWrapper {
	private static final Class[] ACCEPTS = new Class[]{BasicStroke.class};
	
	@Override
	public Class[] appliesTo() {return ACCEPTS;}
	@Override
	public Object convert(Object v, Class c) {
		if (v instanceof StrokeTuple && Stroke.class.isAssignableFrom(c)) {
			return ((StrokeTuple) v).getStroke();
		} else if (v instanceof BasicStroke && c.equals(String.class)) {
			return new StrokeTuple((BasicStroke) v).toString(); 
		} else if (v instanceof StrokeTuple && c.equals(String.class)) {
			return v.toString();
		} else if (v instanceof BasicStroke && c.isAssignableFrom(StrokeTuple.class)) {
			return new StrokeTuple((BasicStroke) v);
		} else if (Stroke.class.isAssignableFrom(c) || c.equals(StrokeTuple.class)) {
			return StrokeUtils.Stroke.parse(v.toString());
		}
		throw new ConversionException(v,c);
	}

	@Override
	public Tuple toTuple(Object o) {
		if (o instanceof BasicStroke) {return new StrokeTuple(((BasicStroke) o));}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
