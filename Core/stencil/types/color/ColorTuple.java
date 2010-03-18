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

import java.util.Arrays;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import static stencil.parser.ParserConstants.SEPARATOR;
import static stencil.parser.ParserConstants.SIGIL;
import static stencil.types.color.ColorCache.OPAQUE_INT;

public final class ColorTuple extends java.awt.Color implements Tuple {
	private static final String SELF_FIELD = "self";
	private static final String RED_FIELD = "Red";
	private static final String GREEN_FIELD = "Green";
	private static final String BLUE_FIELD = "Blue";
	private static final String ALPHA_FIELD = "Alpha";
	private static final String[] FIELDS = new String[]{SELF_FIELD, RED_FIELD, GREEN_FIELD, BLUE_FIELD, ALPHA_FIELD};
	private static final Class[] TYPES = new Class[]{ColorTuple.class, Integer.class, Integer.class, Integer.class, Integer.class};
	private static final TuplePrototype PROTOTPYE = new SimplePrototype(FIELDS, TYPES);

	public static final int SELF   = Arrays.asList(FIELDS).indexOf(SELF_FIELD);
	public static final int RED   = Arrays.asList(FIELDS).indexOf(RED_FIELD);
	public static final int GREEN = Arrays.asList(FIELDS).indexOf(GREEN_FIELD);
	public static final int BLUE  = Arrays.asList(FIELDS).indexOf(BLUE_FIELD);
	public static final int ALPHA = Arrays.asList(FIELDS).indexOf(ALPHA_FIELD);
		
	ColorTuple(int color) {super(color, true);}
	
	public TuplePrototype getPrototype() {return PROTOTPYE;}

	public boolean isDefault(String name, Object value) {
		if (!(value instanceof Number)) {return false;}
		
		if (value instanceof Double) {
			double v = ((Double) value).doubleValue();
			if (name.equals(ALPHA_FIELD)) {return v == 1.0;}
			else {return v == 0.0;}
		} else {
			int v = ((Number) value).intValue();
			if (name.equals(ALPHA_FIELD)) {return v == 255;}
			else {return v == 0;}
		}		
	}
	
	public static String toString(java.awt.Color source) {
		StringBuilder b = new StringBuilder(SIGIL);
		b.append("Color{");
		b.append(source.getRed());
		b.append(SEPARATOR);
		b.append(source.getGreen());
		b.append(SEPARATOR);
		b.append(source.getBlue());
		
		
		if (source.getAlpha() != OPAQUE_INT) {
			b.append(SEPARATOR);
			b.append(source.getAlpha());
		}
		
		b.append("}");

		return b.toString();
	}

	public String toString() {return toString(this);}
	
	public Object get(String name) {
		if (SELF_FIELD.equals(name)) {return get(SELF);}
		if (RED_FIELD.equals(name)) {return get(RED);}
		if (GREEN_FIELD.equals(name)) {return get(GREEN);}
		if (BLUE_FIELD.equals(name)) {return get(BLUE);}
		throw new InvalidNameException(name, getPrototype());
	}
	
	public int size() {return FIELDS.length;}
	public Object get(int idx) {
		if (idx == SELF) {return this;}
		if (idx == RED) {return getRed();}
		if (idx == GREEN) {return getGreen();}
		if (idx == BLUE) {return getBlue();}
		if (idx == ALPHA) {return getAlpha();}
		throw new TupleBoundsException(idx, size());
	}
	
	public ColorTuple modify(int field, Integer value) {
		int c = this.getRGB();
		
		if (field == RED)   {c = IntColor.modifyRed(c, value.intValue());}
		if (field == GREEN) {c = IntColor.modifyGreen(c, value.intValue());}
		if (field == BLUE)  {c = IntColor.modifyBlue(c, value.intValue());}
		if (field == ALPHA) {c = IntColor.modifyAlpha(c, value.intValue());}
		if (field == SELF)  {throw new IllegalArgumentException("Cannot modify self field.");}
		return ColorCache.toTuple(c);
	}
}
