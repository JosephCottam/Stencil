package stencil.types.color;

import java.awt.Color;
import java.awt.Paint;
import java.util.Arrays;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import stencil.types.TypeWrapper;
import stencil.util.collections.ArrayUtil;

public final class ColorWrapper implements TypeWrapper<ColorTuple> {
	private static final Class[] ACCEPTS = {Color.class, Paint.class};
	
	public Class[] appliesTo() {return ACCEPTS;}

	public Object convert(Object value, Class c) {
		String v = value.toString();
		if (v.startsWith("@")) {
			v=v.substring(v.indexOf("{")+1, v.lastIndexOf("}"));
		}
		
		if (c.equals(Color.class) || c.equals(Paint.class)) {return ColorCache.get(v);}
		if (c.equals(String.class) && value instanceof ColorTuple) {return value.toString();}
		if (c.equals(String.class) && value instanceof Color) {return ColorCache.get((Color) value).toString();}
		
		throw new RuntimeException("error converting...");	
	}
	
	public Color external(Tuple t, TuplePrototype p, Class c) {
		if (t instanceof ColorTuple) {return (ColorTuple) t;}//ColorTuple implements the accepted classes, it can always be returned
		
		
		String[] names = TuplePrototypes.getNames(p);

		//Try for a named color
		int nameIdx = ArrayUtil.indexOf("Color", names);
		if (nameIdx >0){return ColorCache.get(t.get(nameIdx).toString());}
		
		//Try for an RGB color
		int R = Converter.toInteger(ArrayUtil.indexOf("R", names));
		int G = Converter.toInteger(ArrayUtil.indexOf("G", names));
		int B = Converter.toInteger(ArrayUtil.indexOf("B", names));
		int A =	Converter.toInteger(ArrayUtil.indexOf("A", names));
		if (A<0) {A=ColorCache.OPAQUE_INT;}
		
		if (!(R<0 || G<0 || B<0)) {
			Color color = new Color(R,G,B,A);
			return ColorCache.get(color);
		}
		
		
		float H = Converter.toFloat(ArrayUtil.indexOf("H", names));
		float S = Converter.toFloat(ArrayUtil.indexOf("S", names));
		float V = Converter.toFloat(ArrayUtil.indexOf("V", names));
		if (!(H<0 || S<0 || V<0)) {
			Color color = Color.getHSBColor(H, S, V);
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), A);
			return ColorCache.get(color);
		}

		throw new RuntimeException("Could not construct color from: " + Arrays.deepToString(TuplePrototypes.getNames(p)));		
	}

	public ColorTuple toTuple(Object o) {
		if (o instanceof ColorTuple) {return (ColorTuple) o;}
		if (o instanceof Color) {return ColorCache.get((Color) o);}
		else {return ColorCache.get(o.toString());}
	}

}
