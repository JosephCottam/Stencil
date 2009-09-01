package stencil.types.color;

import static stencil.types.Converter.*;

import java.awt.Color;
import java.util.List;

import stencil.parser.tree.Value;

/**Class for constructing colors from ints.
 * Based on the java.awt.Color implementation originally,
 * influenced by the Prefuse ColorLib implementation.
 * 
 * @author jcottam
 *
 */
final class IntColor {

	static int modifyRed(int c, int r) {
		return colorInt(r, getGreen(c), getBlue(c), getAlpha(c));
	}

	static int modifyGreen(int c, int g) {
		return colorInt(getRed(c), g, getBlue(c), getAlpha(c));
	}

	
	static int modifyBlue(int c, int b) {
		return colorInt(getRed(c), getGreen(c), b, getAlpha(c));
	}
	
	/**Ensure the alpha value of a color representation.  
	 * If the current alpha is the same as the desired alpha, the original
	 * color is returned unmodified. 
	 */
	static int modifyAlpha(int c, int alpha) {
		return colorInt(getRed(c), getGreen(c), getBlue(c), alpha);
	}
	
	static int colorInt(int r, int g, int b) {
		return colorInt(r,g,b,stencil.types.color.Color.OPAQUE_INT);
	}
	static int colorInt(int r, int g, int b, int a) {
		 return ((a & 0xFF) << 24) |
         		((r & 0xFF) << 16) |
         		((g & 0xFF) << 8)  |
         		((b & 0xFF) << 0);		
	}
	
	static int getRed(int c) {return (c >> 16) & 0xFF;}
	
	static int getGreen(int c) {return (c >> 8) & 0xFF;}

	static int getBlue(int c) {return c & 0xFF;}

	static int getAlpha(int c) {return (c >> 24) & 0xFF;}
	

	/**Create a color from HSVA specification.
	 * All arguments are floats.
	 */
	static int HSVA(List<Value> args) {
		float h = toFloat(args.get(0));
		float s = toFloat(args.get(1));
		float b = toFloat(args.get(2));
		float a = toFloat(args.get(3));
		
		int alpha =  ColorUtils.rangeValue(a);
		
		int c= Color.HSBtoRGB(h, s, b);
		return modifyAlpha(c, alpha);
	}
	
	/**Create a color from an RGBA specification.
	 * All values are integers;
	 */
	static int RGBA(List<Value> args) {
		int r = ColorUtils.rangeValue(args.get(0));
		int g = ColorUtils.rangeValue(args.get(1));
		int b = ColorUtils.rangeValue(args.get(2));
		int a = ColorUtils.rangeValue(args.get(3));
		return IntColor.colorInt(r, g, b, a);
	}
}
