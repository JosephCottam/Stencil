package stencil.types.color;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import stencil.parser.ParserConstants;
import stencil.types.TypeCreationException;

public final class ColorCache {
	public static final int CLEAR_INT = 0;
	public static final int OPAQUE_INT = 255;
	public static final float OPAQUE_FLOAT = 1.0f;

	public static final boolean isTransparent(java.awt.Paint c) {
		if (c instanceof java.awt.Color) {return ((java.awt.Color) c).getAlpha()==CLEAR_INT;}
		return false;
	}

	public static final boolean isOpaque(java.awt.Paint c) {
		if (c instanceof java.awt.Color) {return ((java.awt.Color) c).getAlpha()==OPAQUE_INT;}
		return false;
	}
	
	private static final List<String> HSB = Arrays.asList("HSV", "HSVA", "HSB", "HSBA");
	private static final List<String> RGB = Arrays.asList("RGB", "RGBA");
		
	/**Cache of seen colors. Cache idea is also implemented in Prefuse, this implementation may migrate more towards its.*/
	static HashMap<Integer, ColorTuple> cache = new HashMap<Integer, ColorTuple>();

	public static ColorTuple get(java.awt.Color color) {return toTuple(color.getRGB());}
	public static ColorTuple get(String arg) {
		String[] args = arg.trim().split("[" + ParserConstants.SEPARATOR + ":\\s)]+");
		int color;
		try {
			if (args.length <=2) {
				color = NamedColor.create(args);
				return toTuple(color);
			} 			
			
			String type = args[0];			
			if (HSB.contains(type)) {color = IntColor.HSVA(ensureAlpha(trimType(args), false));}
			else if (RGB.contains(type)) {color = IntColor.RGBA(ensureAlpha(trimType(args), true));}
			else {color = IntColor.RGBA(ensureAlpha(args, isInt(args[0])));}
			
			return toTuple(color);
			
		} catch (Exception e) {throw new TypeCreationException(args, e);}
	}
	
	private static String[] trimType(String[] args) {return Arrays.copyOfRange(args, 1, args.length);}
	
	private static boolean isInt(String v) {try {Integer.parseInt(v); return true;} catch (Exception e) {return false;}}
	private static String[] ensureAlpha(String[] args, boolean integer) {
		assert args.length ==3 || args.length ==4 : "Incorrect number of values passed (must be 3 or 4): " + args.length;
		if (args.length ==3) {
			String[] extended = Arrays.copyOf(args, 4);
			if (integer) {extended[3] = Integer.toString(OPAQUE_INT);}
			else { extended[3] = Float.toString(OPAQUE_FLOAT);}
			args = extended;
 		}
		return args;
	}

	static ColorTuple toTuple(int color) {
		if (cache.containsKey(color)) {
			ColorTuple rv = cache.get(color);
			return rv;
		}
		
		ColorTuple value = new ColorTuple(color);
		cache.put(color, value);
		return value;
	}
	
	/**Return the tuple representation of a color.*/
	public ColorTuple toTuple(java.awt.Color source) {
		int rgb = source.getRGB();
		return toTuple(rgb);
	}
	
	/**Gets a string representation of an AWT color.*/
	public String toString(java.awt.Color source) {return toTuple(source.getRGB()).toString();}

}
