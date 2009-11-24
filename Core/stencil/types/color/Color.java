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
package stencil.types.color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import stencil.types.SigilType;
import stencil.types.TypeCreationException;
import stencil.parser.tree.*;
import stencil.parser.string.ParseStencil;
import stencil.util.ConversionException;

public final class Color implements SigilType<java.awt.Color, ColorTuple> {
	public static final int CLEAR_INT = 0;
	public static final int OPAQUE_INT = 255;

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
	
	public Color(String... parameters) {
		if (parameters.length >0 ) {throw new RuntimeException("Color takes no arguments.");} 
	}
	
	public ColorTuple create(List<Value> args) throws TypeCreationException {
		int color;
		try {
			if (args.size() <=2) {
				color = NamedColor.create(args);
				return toTuple(color);
			} 			
			
			String type = "";
			if (args.get(0).isString()) {type = ((StencilString) args.get(0)).getString();}
			
			if (HSB.contains(type)) {color = IntColor.HSVA(ensureAlpha(trimType(args)));}
			else if (RGB.contains(type)) {color = IntColor.RGBA(ensureAlpha(trimType(args)));}
			else {color = IntColor.RGBA(ensureAlpha(args));}
			
			return toTuple(color);
			
		} catch (Exception e) {throw new TypeCreationException(args, e);}
	}
	
	private List<Value> trimType(List<Value> args) {return args.subList(1, args.size());}
	
	private List<Value> ensureAlpha(List<Value> args) {
		if (args.size() ==4) {return args;}
		if (args.size() ==3) {
			List<Value> v = new ArrayList<Value>(args);
			
			v.add(Atom.Literal.instance(OPAQUE_INT));
			args = v;
 		}
		
		return args;
	}
	
	public java.awt.Color toExternal(ColorTuple source) {return source;}

	public ColorTuple toTuple(int color) {return internalToTuple(color);}
	
	static ColorTuple internalToTuple(int color) {
		if (cache.containsKey(color)) {return cache.get(color);}
		
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
	
	/**Converts to/from color objects.*/
	public Object convert(Object value, Class target) {
		ColorTuple t = null;
		if (value instanceof Sigil) {return convert(((Sigil) value).getValue(), target);}
		else if (value instanceof ColorTuple) {t = (ColorTuple) value;}
		else if (value instanceof java.awt.Color) {t = toTuple(((java.awt.Color) value).getRGB());}
		else if (value instanceof Integer) {t =toTuple((Integer) value);}
		else if (value instanceof String) { 
			Sigil s;
			try {s = ParseStencil.parseSigil((String) value);}
			catch (Exception e) {throw new ConversionException(value, target, e);}
			t = (ColorTuple) s.getValue();
		}
		
		if (t == null) {throw new ConversionException(value, target);}
		
		
		if (target.isAssignableFrom(ColorTuple.class)) {return t;}
		if (target.equals(Integer.class)) {return t.getRGB();}
		if (target.equals(java.lang.String.class)) {return toString(t);}

		throw new ConversionException(value, target);
	}
}
