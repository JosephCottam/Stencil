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


import java.awt.Component;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Font;

import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.enums.Attribute;
import stencil.util.enums.EnumUtils;

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class TextFormats {
	
	/**Class for the multiple return values.*/
	public static final class Format {
		public final Font font; 
		public final Color textColor;
		public final float justification;

		public Format() {	
			String fontName =(String) TextProperty.FONT.defaultValue;
			int style =((FontStyle) TextProperty.FONT_STYLE.defaultValue).equiv;
			float size = (Float) TextProperty.FONT_SIZE.defaultValue;
			
			Font temp =  new Font(fontName, style, 1);
			font = temp.deriveFont(style, size);
			textColor =(Color) TextProperty.FONT_COLOR.defaultValue;
			justification= ((Justification) TextProperty.JUSTIFY.defaultValue).equiv;
		}
		
		public Format(Font font, Color textColor, float justification) {
			this.font = font;
			this.textColor = textColor;
			this.justification = justification;
		}
	}

	
	/**Text justification values that are recognized**/
	public static enum Justification {
		LEFT (Component.LEFT_ALIGNMENT),
		RIGHT (Component.RIGHT_ALIGNMENT),
		CENTER (Component.CENTER_ALIGNMENT);

		float equiv;
		Justification(float equiv){this.equiv = equiv;}
		public float getJLabelEquiv() {return equiv;}
		public static Justification fromJLabelEquiv(float equiv) {
			for (Justification j: Justification.values()) {
				if (j.equiv == equiv) {return j;}
			}
			throw new IllegalArgumentException("No justification equivalent found for " + equiv + ".");
		}
	}


	/**Recognized font styles.  Equivalents can be found in the java.awt.Font constants.**/
	public static enum FontStyle {
		PLAIN (Font.PLAIN),
		BOLD (Font.BOLD),
		ITALIC (Font.ITALIC),
		BOLD_ITALIC (Font.BOLD + Font.ITALIC);

		int equiv;
		FontStyle(int equiv) {this.equiv = equiv;}
		public int getFontEquiv() {return equiv;}
		public static FontStyle fromFontEquiv(int equiv) {
			for (FontStyle s: FontStyle.values()) {if (s.equiv == equiv) {return s;}}
			throw new IllegalArgumentException("No style equivalent found for " + equiv + ".");
		}
	}

	public enum TextProperty implements Attribute {
		JUSTIFY	 	(Justification.LEFT, Justification.class),
		FONT		("Helvetica", String.class),
		FONT_COLOR  (Color.BLACK, Color.class),
		FONT_STYLE	(FontStyle.PLAIN, FontStyle.class),
		FONT_SIZE	(12.0f, Float.class);			//Should be a round integer by default to be properly supported in default format

		final Object defaultValue;
		final Class type;

		TextProperty(Object defaultValue, Class type)  {
			this.defaultValue = defaultValue;
			this.type = type;
		}

		public Class getType() {return type;}
		public Object getDefaultValue() {return defaultValue;}
	}

	private TextFormats() {/*Utility class. Not instantiable.*/}

	public static final Format make(Tuple... sources) {
		String name = (String) TextProperty.FONT.defaultValue;
		FontStyle style = (FontStyle) TextProperty.FONT_STYLE.defaultValue;;
		Float size = (Float) TextProperty.FONT_SIZE.defaultValue;
		Color color = (Color) TextProperty.FONT_COLOR.defaultValue;
		Justification just = (Justification) TextProperty.JUSTIFY.defaultValue;
		
		for (Tuple t: sources) {
			for (String field: t.getPrototype()) {
				if (EnumUtils.contains(TextProperty.class, field)) {
					TextProperty p = TextProperty.valueOf(field);
					Object value = t.get(field);
					
					switch(p) {
						case FONT:
							name = Converter.convertFor(value, name); 
							break;
						case FONT_COLOR:
							color = Converter.convertFor(value, color);
							break;
						case FONT_SIZE:
							size = Converter.convertFor(value, size);
							break;
						case FONT_STYLE:
							style = Converter.convertFor(value, style);
							break;
						case JUSTIFY:
							just = Converter.convertFor(value, just);
							break;
					}
					
				}
			}
		}
		
		Font f = new Font(name, style.equiv, 1);
		f = f.deriveFont(style.equiv, size);
		
		return new Format(f, color, just.equiv);
	}
	
	/**Given the base font, return a new font with the specified name/size/style.
	 * Null values indicate that the current value should be retained in the base.
	 * The original font is not modified (as Fonts are invariant), instead a new
	 * copy is retained UNLESS all values are null or equal to their current value
	 * (i.e. this call would not actually modify the base font).
	 * If the call would make no change to the base font, then the original is returned.
	 *
	 * @param base
	 * @param name
	 * @param size
	 * @param style
	 * @return
	 */
	public static Font modifyFont(Font base, String name, Float size, Integer style) {
		assert base != null : "Cannot pass null font to modifyFont.";

		//Check for easy return case
		if (name == null && size == null && style == null) {return base;}
		if (base.getName().equals(name) && base.getSize() == size && base.getStyle() == style) {return base;}

		if (name == null) {name = base.getName();}
		if (size == null) {size = new Float(base.getSize2D());}
		if (style == null) {style = base.getStyle();}
		Font newFont = new Font(name, style, 10);
		newFont = newFont.deriveFont(size.floatValue());

		return newFont;
	}

	public static Format set(String key, Object value, Format format) {
		return set(key, value, format.font, format.textColor, format.justification);
	}

	public static Format set(String key, Object value, Font font, Paint color, float justify) {
		String name = font.getName();
		float size =  font.getSize2D();
		int style = font.getStyle();

		TextProperty att = TextProperty.valueOf(key.toString());
		switch (att) {
			case JUSTIFY: justify = ((Justification) value).getJLabelEquiv(); break;
			case FONT: name = (String) value; break;
			case FONT_COLOR: color = (Color) value; break;
			case FONT_STYLE: style = ((FontStyle) value).getFontEquiv(); break;
			case FONT_SIZE: size = (Float) value; break;
		}

		font = modifyFont(font, name, size, style);
		return new Format(font, (Color) color, justify);

	}

	public static Object get(String key, Format format) {
		return get(key, format.font, format.textColor, format.justification);
	}
	public static Object get(String key, Font font, Paint color, float justify) {
		TextProperty att = TextProperty.valueOf(key.toString());

		switch (att) {
			case JUSTIFY: return Justification.fromJLabelEquiv(justify);
			case FONT: return font.getName();
			case FONT_COLOR: return color;
			case FONT_STYLE: return FontStyle.fromFontEquiv(font.getStyle());
			case FONT_SIZE: return new Float(font.getSize2D());
			default: throw new IllegalArgumentException("Could not handle key " + key + " in text format handler.");
		}
	}
	
}
