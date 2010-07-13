package stencil.types.stroke;

 import java.awt.BasicStroke;
import java.awt.Stroke;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;
import static stencil.types.stroke.StrokeTuple.*;

/**Pen is the pattern information about a stroke or
 * a fill, but not its color elements (those are in "Paint").*/
public class StrokeWrapper implements TypeWrapper {
	private static final Class[] ACCEPTS = new Class[]{BasicStroke.class};
	
	public Class[] appliesTo() {return ACCEPTS;}
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

	public Object external(Tuple t, TuplePrototype p, Class c) {
		if (c.isAssignableFrom(Stroke.class)) {
			if (t instanceof StrokeTuple) {return ((StrokeTuple) t).getStroke();}
			
			float width = Tuples.safeGet(WIDTH, t, p, DEFAULT_WIDTH);
			Join join = Tuples.safeGet(JOIN, t, p, DEFAULT_JOIN);
			Cap cap = Tuples.safeGet(CAP, t, p, DEFAULT_CAP);
			float phase = Tuples.safeGet(PHASE, t, p, DEFAULT_PHASE);
			float limit = Tuples.safeGet(LIMIT, t, p, DEFAULT_LIMIT);
			
			float[] pattern =Pattern.SOLD.mask;
			if (p.contains(PATTERN)) {
				Object pat = t.get(PATTERN);
				if (pat instanceof Pattern) {
					pattern = ((Pattern) pat).mask;
				} else if (pat instanceof String){
					pattern = Pattern.valueOf((String) pat).mask;
				} else {
					pattern = (float[]) pat;
				}
			}
			
			return new BasicStroke(width, cap.v, join.v, limit, pattern, phase);
		}			
		
		throw new RuntimeException("Could not externalize for Stroke: " + t.toString());
	}

	public Tuple toTuple(Object o) {
		if (o instanceof BasicStroke) {return new StrokeTuple(((BasicStroke) o));}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
