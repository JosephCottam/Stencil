package stencil.types.font;

import java.awt.Font;

import stencil.tuple.Tuple;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;

public class FontWrapper implements TypeWrapper {
	private static Class[] ACCEPTS = {Font.class};

	public Class[] appliesTo() {return ACCEPTS;}

	public Object convert(Object v, Class c) {
		if (c.isAssignableFrom(v.getClass())) {return v;}
		
		if (v instanceof Font && c.equals(String.class)) {
			return new FontTuple((Font) v).toString();
		}
		
		if (v instanceof Font && c.equals(FontTuple.class)) {
			return new FontTuple((Font) v);
		}
		
		if (v instanceof FontTuple && c.equals(Font.class)) {return ((FontTuple) v).getFont();}

		try {
			if (c.equals(FontTuple.class)) {
				return FontUtils.Font.toTuple(v.toString());
			} else if (c.equals(Font.class)) {
				return FontUtils.Font.toTuple(v.toString()).getFont();
			}
		} catch (Exception e) {}
		throw new ConversionException(v,c);
	}

	public Tuple toTuple(Object o) {
		if (o instanceof Font) {return new FontTuple((Font) o);}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
