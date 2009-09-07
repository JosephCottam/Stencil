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
package stencil.adapters.java2D.data.glyphs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import stencil.adapters.general.Pies;
import stencil.adapters.general.Strokes.StrokeProperty;
import stencil.adapters.java2D.util.AttributeList;
import stencil.adapters.java2D.util.Attribute;
import stencil.types.Converter;

public final class Pie extends Stroked {
	protected static final Color DEFAULT_SLICE_COLOR  = Color.RED;
	protected static final Color DEFAULT_FIELD_COLOR  = Color.WHITE;

	
	private static final Attribute PERCENT = new Attribute("PERCENT", 0.5d);
	private static final Attribute SLICE = new Attribute("SLICE", 0.5d);
	private static final Attribute FIELD = new Attribute("FIELD", 0.5d);
	private static final Attribute SLICE_COLOR = new Attribute("SLICE_COLOR", DEFAULT_SLICE_COLOR, Paint.class);
	private static final Attribute FIELD_COLOR = new Attribute("FIELD_COLOR", DEFAULT_FIELD_COLOR, Paint.class);
	private static final Attribute ANGLE = new Attribute("ANGLE", 0.0d);
	private static final Attribute SIZE = new Attribute("SIZE", 1.0d);
	
	protected static final AttributeList attributes;
	
	static {
		attributes = new AttributeList(Stroked.attributes);

		attributes.add(PERCENT);
		attributes.add(SLICE);
		attributes.add(FIELD);
		attributes.add(SLICE_COLOR);
		attributes.add(ANGLE);
		attributes.add(SIZE);
		attributes.add(FIELD_COLOR);
	}

	private double size = (Double) SIZE.defaultValue;	
	private Paint slicePaint = (Paint) SLICE_COLOR.defaultValue;
	private Paint fieldPaint = (Paint) FIELD_COLOR.defaultValue;

	private double angle;
	
	private double field;
	private double slice;
	
	public Pie(String id) {super(id);}
	
	public void set(String name, Object value) {
		if (PERCENT.is(name)) {double p = Converter.toDouble(value); slice = p; field = 1-p;}
		else if (SLICE.is(name)) {slice = Converter.toDouble(value);}
		else if (FIELD.is(name)) {field = Converter.toDouble(value);}
		else if (ANGLE.is(name)) {angle = Converter.toDouble(value);}
		else if (SIZE.is(name)) {size = Converter.toDouble(size);}
		else if (SLICE_COLOR.is(name)) {slicePaint = (Paint) Converter.convert(value, Paint.class);}
		else if (FIELD_COLOR.is(name)) {fieldPaint = (Paint) Converter.convert(value, Paint.class);}
		else {super.set(name, value);}
	}
	
	public Object get(String name) {
		if (PERCENT.is(name)) {return getPercent();} 
		if (SLICE.is(name)) {return slice;}
		if (FIELD.is(name)) {return field;}
		if (SLICE_COLOR.is(name)) {return slicePaint;}
		if (FIELD_COLOR.is(name)) {return fieldPaint;}
		if (ANGLE.is(name)) {return angle;}
		if (SIZE.is(name)) {return name;}
		
		return super.get(name);
	}
	
	protected AttributeList getAttributes() {return attributes;}

	private double getPercent() {return slice/(slice+field);}
	public double getHeight() {return size;}
	public double getWidth() {return size;}
	public String getImplantation() {return "PIE";}


	public void render(Graphics2D g) {
		double angle = this.angle;
		double percent = getPercent();
		double x = this.x;
		double y = this.y;
		double size = this.size;
		double strokeWidth = (Double) get(StrokeProperty.STROKE_WEIGHT.name());
		Color strokePaint = (Color) outlinePaint;
		
		java.awt.Shape arc = Pies.makeSlice(angle, percent, x, y, size, strokeWidth, strokePaint);
		java.awt.Shape outline = Pies.makePieOutline(angle, percent, x, y, size, strokeWidth, strokePaint);


		g.setPaint(fieldPaint);
		g.fill(outline);
		
		if (arc != null) {
			g.setPaint(slicePaint);
			g.fill(arc);
		}
		
		g.setPaint(outlinePaint);
		g.setStroke(outlineStyle);
		g.draw(outline);
	}

}
