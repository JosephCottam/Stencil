/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.adapters.general;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;

import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.enums.Attribute;
import stencil.util.enums.EnumUtils;

/**General purpose fill items, covers both textures and solid fills.
 **/

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Fills {

	/**Class to encapsulate a texture that can still be unpacked
	 * and changed component-wise.  Values not used in the
	 * required paint are still retained (i.e. cached) so changes
	 * can be made in stages.
	 * 
	 * TODO: Add gradients to the mix
	 */
	public static class CachePaint implements Paint {
		public static final CachePaint DEFAULT = new CachePaint();
		protected Pattern pattern;
		protected int scale;
		protected double weight;
		protected Color fore;
		protected Color back;

		protected Paint paint;


		private CachePaint() {
			this((Pattern) FillProperty.PATTERN.getDefaultValue(),
				 (Integer) FillProperty.PATTERN_SCALE.getDefaultValue(),
				 (Double) FillProperty.PATTERN_WEIGHT.getDefaultValue(),
			 	 (Color) FillProperty.FILL_COLOR.getDefaultValue(),
				 (Color) FillProperty.PATTERN_BACK.getDefaultValue());
		}

		public CachePaint(Pattern pattern, int scale, double weight, Color fore, Color back) {
			this (pattern, scale, weight, fore, back, null, null);
			assert pattern == Pattern.SOLID : "Can only use simple constructor with Solid patterns.";
			paint = fore;
		}

		public CachePaint(Pattern pattern, int scale, double weight, Color fore, Color back,BufferedImage i, Rectangle2D anchor) {
			this.pattern = pattern;
			this.scale = scale;
			this.weight = weight;
			this.fore = fore;
			this.back = back;

			if (pattern == Pattern.HATCH) {
				assert i != null : "Cannot use a null image with a hatched paint";
				assert anchor != null : "Cannot use a null amchor with a hatched paint";

				paint = new TexturePaint(i, anchor);}
			else {
				paint = fore;
			}
		}

		public String toString() {
			switch(pattern){
				case SOLID: return String.format("%1$s : %2$s", pattern, fore);
				default: return String.format("%1$s : %2$d : %3$f : %4$s : %5$s", pattern, scale, weight, fore, back.toString());
			}
		}

		/**Do relevant aspects match?  Only compares those used by the type.
		 */
		public boolean equals(Object other) {
			if (this == other) {return true;}
			if (other == null || !(other instanceof CachePaint)) {return false;}
			CachePaint o= (CachePaint) other;

			if (pattern == Pattern.SOLID) {return fore.equals(o.fore);}
			
			return (pattern.equals(o.pattern)) &&
				   (scale == o.scale) &&
				   (weight == o.weight) &&
				   (fore.equals(o.fore)) &&
				   (back.equals(o.back));
		}

		public int hashCode() {return (int) ((pattern.hashCode() * scale)/weight);}

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			if (hints ==null) {hints = new RenderingHints(null);} //TODO: Not sure why this is required to do a textured paint, but it is!
			return paint.createContext(cm, deviceBounds, userBounds, xform, hints);
		}

		public int getTransparency() {return paint.getTransparency();}

		/**The cache paint has an underlying paint...return that here.*/
		public Paint getBasePaint() {return paint;}
	}
	
	/**Items that can be used as 'PATTERN' attribute*/
	public static enum Pattern {HATCH, SOLID};

	public static enum FillProperty implements Attribute {
		PATTERN(Pattern.SOLID, Pattern.class),
		PATTERN_SCALE(10, Integer.class),
		PATTERN_WEIGHT(1.0d, Double.class),
		PATTERN_BACK(Color.WHITE, Color.class),
		FILL_COLOR(Color.BLACK, Color.class);			//Will be used as the foreground if pattern is empty or null

		protected final Object defaultValue;
		protected final Class type;

		FillProperty(Object defaultValue, Class type)  {
			this.defaultValue = defaultValue;
			this.type = type;
		}

		public Class getType() {return type;}
		public Object getDefaultValue() {return defaultValue;}
	}


	private Fills() {/*Utility class.  Not instantiable.*/}

	/**Get a paint that conforms to all defined defaults.*/
	public static Paint getDefault() {return CachePaint.DEFAULT;}

	/**Given a paint, get a string representation.  Can handle all paints, but
	 * not necessarily in a reconstructible way.
	 *
	 * @param p
	 * @return
	 */
	public static String fillString(Paint p) {
		if (p instanceof CachePaint) {return p.toString();}
		return "Custom Texture: " +p.toString();
	}

	/**Make a paint from the list of tuples.  Tuples are evaluated
	 * in order so precedence is given to later tuples.  The 
	 * starting point is the default paint.
	 */
	public static Paint make(Tuple... sources) {
	
		Pattern pattern = (Pattern) FillProperty.PATTERN.defaultValue;
		int scale = (Integer) FillProperty.PATTERN_SCALE.defaultValue;
		double weight = (Double) FillProperty.PATTERN_WEIGHT.defaultValue;
		Color fore = (Color) FillProperty.FILL_COLOR.defaultValue;
		Color back = (Color) FillProperty.PATTERN_BACK.defaultValue;
		
		for (Tuple t: sources) {
			for (String f: t.getPrototype()) {
				if (EnumUtils.contains(FillProperty.class, f)) {
					FillProperty att = FillProperty.valueOf(f);
					Object value = t.get(f);
					switch (att) {
						case PATTERN: pattern = Converter.convertFor(value, pattern); break;
						case PATTERN_SCALE: scale = Converter.convertFor(value, scale); break;
						case PATTERN_WEIGHT: weight = Converter.convertFor(value, weight); break;
						case PATTERN_BACK: back = Converter.convertFor(value, back); break;
						case FILL_COLOR:  fore = Converter.convertFor(value, fore); break;
					}
				}
			}
		}
		
		return getFill(pattern, scale, weight, fore, back);
	}
	
	public static CachePaint getFill(Pattern p, int scale, double weight, Color fore, Color back) {
		switch(p) {
			case HATCH: return hatch(scale, weight, fore, back);
			case SOLID: return solid(scale, weight, fore, back);
			default: throw new IllegalArgumentException("Unknown fill type: " + p.toString());
		}
	}

	public static Paint modify(Paint b, String key, Object value) {
		FillProperty att = FillProperty.valueOf(key.toString());
		return modify(b, att, value);
	}
	
	public static Paint modify(Paint b, FillProperty att, Object value) {
		CachePaint base = (CachePaint) b;
		
		Pattern p = base.pattern;
		Integer scale = base.scale;
		Double weight = base.weight;
		Color fore = base.fore;
		Color back = base.back;

		switch(att) {
			case PATTERN: p = Pattern.valueOf(value.toString()); break;
			case PATTERN_SCALE: scale = Converter.toInteger(value); break;
			case PATTERN_WEIGHT: weight = Converter.toDouble(value); break;
			case PATTERN_BACK: back = Converter.convertFor(value, back); break;
			case FILL_COLOR: fore = Converter.convertFor(value, fore); break;
			default: throw new RuntimeException(String.format("Could not find property %1$s on a texture.", att));
		}

		if (p == base.pattern && scale == base.scale && weight == base.weight
				&& fore.equals(base.fore) && back.equals(base.back)) {
			return base;
		} 
		return getFill(p, scale, weight, fore, back);
	}

	/**Returns a value from the paint.  If the paint is null,
	 * it is assumed that the default is desired and that will be
	 * returned instead.
	 *
	 * @param key
	 * @param b
	 * @return
	 */
	public static Object get(Object key, Paint b) {
		CachePaint base = (CachePaint) b;

		FillProperty att;
		if (key instanceof FillProperty) {att = (FillProperty) key;}
		else {att = FillProperty.valueOf(key.toString());}

		switch (att) {
		case PATTERN: return base.pattern;
		case PATTERN_SCALE: return base.scale;
		case PATTERN_WEIGHT: return base.weight;
		case PATTERN_BACK: return base.back;
		case FILL_COLOR: return base.fore;
		default: throw new RuntimeException(String.format("Could not find property %1$s on a texture.", att));
		}
	}

	public static CachePaint solid(int scale, double weight, Color fore, Color back) {
		return new CachePaint(Pattern.SOLID, scale, weight, fore, back);
	}
	
	public static CachePaint hatch(int scale, double weight, Color fore, Color back) {
		BufferedImage i = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) i.getGraphics();

		//Background
		g.setBackground(back);
		g.clearRect(0,0, scale, scale);

		//Foreground
		g.setColor(fore);
		g.setStroke(new BasicStroke((float) weight));
		g.draw(new Line2D.Double(0, 0, scale, scale));
		g.draw(new Line2D.Double(0, scale, scale,0));
		CachePaint p = new CachePaint(Pattern.HATCH, scale, weight, fore, back, i, new Rectangle2D.Double(0,0, i.getWidth(), i.getHeight()));

		return p;
	}

}
