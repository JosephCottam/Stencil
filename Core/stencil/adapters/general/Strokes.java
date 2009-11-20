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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.enums.Attribute;
import stencil.util.enums.EnumUtils;
import stencil.util.enums.ValueEnum;

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Strokes {
	
	private Strokes() {/*Utility class. Not instantiable.*/}
	
	/**Class for returning values from stroke modifying method.
	 * Results need to be unpacked into appropriate locations to be used.
	 * @author jcottam
	 *
	 */
	public static final class ColoredStroke {
		public final Stroke style;
		public final Paint paint;

		public ColoredStroke(Stroke style, Paint paint) {
			this.style = style;
			this.paint = paint;
		}
	}

	/**Default stroke if no attributes are explicitly set*/
	public static final BasicStroke DEFAULT_STROKE = //new BasicStroke(); 
		new BasicStroke(((Double) StrokeProperty.STROKE_WEIGHT.defaultValue).floatValue(),
						((CapStyles) StrokeProperty.CAP_STYLE.defaultValue).equiv,
						((JoinStyles) StrokeProperty.JOIN_STYLE.defaultValue).equiv,
						((Double) StrokeProperty.MITER_LIMIT.defaultValue).floatValue());

	public static final Color DEFAULT_PAINT = new java.awt.Color(0,0,0); //Black, but it needs to be a special instance so we can check if the property has been changed

	/**Cap style enumeration and translator to Basic Stroke equivalents**/
	public static enum CapStyles implements ValueEnum<Integer> {
		BUTT (BasicStroke.CAP_BUTT),
		ROUND (BasicStroke.CAP_ROUND),
		SQUARE (BasicStroke.CAP_SQUARE);

		int equiv;
		CapStyles(int equiv){this.equiv = equiv;}
		public Integer getValue() {return equiv;}
		public static CapStyles fromValue(int equiv) {
			for (CapStyles v: CapStyles.values()) {
				if (v.equiv == equiv) {return v;}
			}
			throw new IllegalArgumentException("No justification equivalent found for " + equiv + ".");
		}
	}

	/**Join style enumeration and translator to Basic Stroke equivalents**/
	public static enum JoinStyles implements ValueEnum<Integer>{
		MITER (BasicStroke.JOIN_MITER),
		BEVEL (BasicStroke.JOIN_BEVEL),
		ROUND (BasicStroke.JOIN_ROUND);

		int equiv;
		JoinStyles(int equiv){this.equiv = equiv;}
		public Integer getValue() {return equiv;}
		public static JoinStyles fromValue(int equiv) {
			for (JoinStyles v: JoinStyles.values()) {
				if (v.equiv == equiv) {return v;}
			}
			throw new IllegalArgumentException("No justification equivalent found for " + equiv + ".");
		}
	}

	/**Stroke attributes supported by this mix-in**/
	public static enum StrokeProperty implements Attribute {
		STROKE_WEIGHT (1d, Double.class),
		CAP_STYLE (CapStyles.ROUND, CapStyles.class),
		JOIN_STYLE (JoinStyles.MITER, JoinStyles.class),
		MITER_LIMIT (10.0d, Double.class),
		STROKE_COLOR (DEFAULT_PAINT, Paint.class);
//		PATTERN ('0' , String.class),
//		PATTERN_PHASE (0, Double.class);

		final Object defaultValue;
		final Class type;

		StrokeProperty(Object defaultValue, Class type)  {
			this.defaultValue = defaultValue;
			this.type = type;
		}

		public Class getType() {return type;}
		public Object getDefaultValue() {return defaultValue;}
	}

	public static Stroke setWeight(Double value, Stroke style) {return modify(StrokeProperty.STROKE_WEIGHT, value, style, null).style;}
	public static Stroke setCapStyle(Double value, Stroke style) {return modify(StrokeProperty.CAP_STYLE, value, style, null).style;}
	public static Stroke setJoinStyle(Double value, Stroke style) {return modify(StrokeProperty.JOIN_STYLE, value, style, null).style;}
	public static Stroke setMiterLimit(Double value, Stroke style) {return modify(StrokeProperty.MITER_LIMIT, value, style, null).style;}
	public static Paint setColor(Double value, Paint paint) {return modify(StrokeProperty.STROKE_WEIGHT, value, null, paint).paint;}

	/**Create a stroke from the given list of tuples.  Tuples are evaluated in order, so later tuples receive
	 * precedence of earlier ones.
	 */
	public static ColoredStroke makeStroke (Tuple... sources) {
		BasicStroke source = DEFAULT_STROKE;
		
		Color strokeColor = DEFAULT_PAINT;
		Double miterLimit = null;
		Double strokeWeight = null;
		Object pattern = null;
		Integer capStyle = null;
		Double patternPhase = null;
		Integer joinStyle = null;
		
		for (Tuple t: sources) {
			for (String f: t.getPrototype()) {
				if (EnumUtils.contains(StrokeProperty.class, f)) {
					StrokeProperty p = StrokeProperty.valueOf(f);
					Object value = t.get(f);
					switch (p) {
						case CAP_STYLE:
							capStyle = Converter.toInteger(value);
							break;
						case JOIN_STYLE:
							joinStyle = Converter.toInteger(value);
							break;
						case MITER_LIMIT:
							miterLimit = Converter.toDouble(value);
							break;
						case STROKE_COLOR:
							strokeColor = Converter.convertFor(value, strokeColor);
							break;
						case STROKE_WEIGHT:
							strokeWeight = Converter.toDouble(value);
							break;
					}
				}
			}
		}		
		
		return new ColoredStroke(Strokes.modifyStroke(source, strokeWeight, capStyle, joinStyle, miterLimit, pattern, patternPhase), strokeColor);
	}  
	
	/**Create a modified style/color pair from passed stroke information.
	 * These methods create new strokes/paints from the ones passed, so the appropriate
	 * stroke and stroke-paint setting methods need to be called with the respective components of the returned ColoredStroke.
	 * @param key Which property to modify
	 * @param value What value to change it to
	 * @param style Base style (may be changed)
	 * @param paint Base color (may be changed);
	 * @return
	 */
	public static ColoredStroke modify(Object key, Object value, Stroke style, Paint paint) {
		String strKey = key.toString();
		StrokeProperty att = StrokeProperty.valueOf(strKey);
		return modify(att, value, style, paint);
	}

	public static ColoredStroke modify(StrokeProperty att, Object value, Stroke style, Paint paint) {
		switch(att) {
		case STROKE_WEIGHT: style = modifyStroke(style, Converter.toDouble(value), null,null,null,null,null); break;
		case CAP_STYLE: style = modifyStroke(style, null, ((CapStyles)Converter.convert(value, CapStyles.class)).equiv,null,null,null,null); break;
		case JOIN_STYLE: style = modifyStroke(style, null, null, ((JoinStyles)Converter.convert(value, JoinStyles.class)).equiv,null,null,null); break;
		case MITER_LIMIT: style = modifyStroke(style, null, null,null, Converter.toDouble(value),null,null); break;
		case STROKE_COLOR: paint = (Color) Converter.convert(value, Color.class); break;
//		case PATTERN: modifyStroke(null, null,null,null,value,null); break;
//		case PATTERN_PHASE: modifyStroke(null, null,null,null,null,(Double) value); break;
		default: throw new IllegalArgumentException("Could not handle key " + att.toString() + " in stroke handler.");
		}

		return new ColoredStroke(style, paint);
	}

	/**Constructs a new stroke based on information passed.  
	 * Any attribute value passed may be null, in which case values
	 * are taken from the source.  If the source is null or not
	 * decomposable (e.g. a BasicStroke) the DEFAULT_STROKE is used.
	 * 
	 * @param source		Stroke to supply values not explicitly passed
	 * @param lineWidth		New line width
	 * @param capStyle		New cap style
	 * @param joinStyle		New join style
	 * @param miterLimit	New miter limit
	 * @param pattern		New pattern (currently ignored)
	 * @param patternPhase	New pattern phase (currently ignored)
	 * @return
	 */
	private static BasicStroke modifyStroke(Stroke source, Double lineWidth, Integer capStyle, Integer joinStyle, Double miterLimit, Object pattern, Double patternPhase) {
		if (!(source instanceof BasicStroke)) {source = DEFAULT_STROKE;}
		BasicStroke current = (BasicStroke) source;

		if (lineWidth == null) {lineWidth = (double) current.getLineWidth();}
		if (capStyle == null) {capStyle = current.getEndCap();}
		if (joinStyle == null) {joinStyle = current.getLineJoin();}
		if (miterLimit == null) {miterLimit = (double) current.getMiterLimit();}
//		if (pattern == null) {pattern = stroke.getDashArray();}
//		if (patternPhase == null) {patternPhase = (double) stroke.getDashPhase();}

		return new BasicStroke(lineWidth.floatValue(), capStyle.intValue(), joinStyle.intValue(), miterLimit.floatValue());
	}

	public static Object get(Object key, Stroke source) {return get(key, source, null);}
	public static Object get(Object key, Paint paint) {return get(key, null, paint);}
	public static Object get(Object key, Stroke source, Paint paint) {
		if (!(source instanceof BasicStroke)) {throw new IllegalArgumentException("Can only use the Strokes utility 'get' with BasicStroke objects.");}
		BasicStroke style = (BasicStroke) source;
		StrokeProperty att;

		if (key instanceof StrokeProperty) {att = (StrokeProperty) key;}
		else {att = StrokeProperty.valueOf(key.toString());}

		switch(att) {
		case STROKE_WEIGHT: return new Double(style.getLineWidth());
		case CAP_STYLE: return CapStyles.fromValue(style.getEndCap());
		case JOIN_STYLE: return JoinStyles.fromValue(style.getLineJoin());
		case MITER_LIMIT: return new Double(style.getMiterLimit());
		case STROKE_COLOR: return paint;
//		case PATTERN:
//		case PATTERN_PHASE:
		default: throw new IllegalArgumentException("Could not handle key " + key + " in stroke handler.");
		}
	}
}
