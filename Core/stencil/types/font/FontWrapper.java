package stencil.types.font;

import java.awt.Font;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;

public class FontWrapper implements TypeWrapper {
	private static Class[] ACCEPTS = {Font.class};

	public Class[] appliesTo() {return ACCEPTS;}

	public Object convert(Object v, Class c) {
		if (v instanceof Font && c.equals(FontTuple.class)) {
			return new FontTuple((Font) v);
		}
		throw new ConversionException(v,c);
	}

	public Object external(Tuple t, TuplePrototype p, Class c) {
		if (c.isAssignableFrom(Font.class)) {
			if (t instanceof FontTuple) {return ((FontTuple) t).getFont();}
			
			String family = Tuples.safeGet(FontTuple.FAMILY, t, p, FontTuple.DEFAULT_FAMILY);
			double size = Tuples.safeGet(FontTuple.SIZE, t, p, FontTuple.DEFAULT_SIZE);
			boolean bold = Tuples.safeGet(FontTuple.SIZE, t, p, FontTuple.DEFAULT_BOLD);
			boolean italic = Tuples.safeGet(FontTuple.SIZE, t, p, FontTuple.DEFAULT_ITALIC);
			return new FontTuple(family, size, bold, italic).getFont();			
		}
		
		throw new RuntimeException("Could not externalize for Font: " + t.toString());
	}

	public Tuple toTuple(Object o) {
		if (o instanceof Font) {return new FontTuple((Font) o);}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
