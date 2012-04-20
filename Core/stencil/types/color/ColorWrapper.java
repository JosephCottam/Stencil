package stencil.types.color;

import java.awt.Color;
import java.awt.Paint;
import stencil.types.TypeWrapper;

public final class ColorWrapper implements TypeWrapper<ColorTuple> {
	private static final Class[] ACCEPTS = {Color.class, Paint.class, ColorTuple.class};
	
	@Override
	public Class[] appliesTo() {return ACCEPTS;}

	@Override
	public Object convert(Object value, Class c) {
		String v = value.toString();
		if (v.startsWith("@")) {
			v=v.substring(v.indexOf("{")+1, v.lastIndexOf("}"));
		}
		
		if (c.equals(Color.class) || c.equals(Paint.class) || c.equals(ColorTuple.class)) {return ColorCache.get(v);}
		if (c.equals(String.class) && value instanceof ColorTuple) {return value.toString();}
		if (c.equals(String.class) && value instanceof Color) {return ColorCache.get((Color) value).toString();}
		
		throw new RuntimeException("error converting...");	
	}
	
	@Override
	public ColorTuple toTuple(Object o) {
		if (o instanceof ColorTuple) {return (ColorTuple) o;}
		if (o instanceof Color) {return ColorCache.get((Color) o);}
		else {return ColorCache.get(o.toString());}
	}

}
