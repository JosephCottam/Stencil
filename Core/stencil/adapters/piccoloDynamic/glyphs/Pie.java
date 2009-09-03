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
package stencil.adapters.piccoloDynamic.glyphs;


import java.awt.Paint;
import java.awt.Color;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Strokes.StrokeProperty;
import stencil.adapters.general.Pies;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;

import edu.umd.cs.piccolo.nodes.PPath;

/**Two-part pie chart.  Uses the path for the field, but provides its own slice.*/
public class Pie extends Path {
	public static final String IMPLANTATION_NAME = "PIE";
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	protected static final Color DEFAULT_SLICE_COLOR  = Color.RED;

	static {
		for (Attribute a : Path.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute("PERCENT", "getPercent", "setPercent", Pie.class, 0.5));
		PROVIDED_ATTRIBUTES.put(new Attribute("SLICE", "getSlice", "setSlice", Pie.class, 0.5));
		PROVIDED_ATTRIBUTES.put(new Attribute("FIELD", "getField", "setField", Pie.class, 0.5));
		PROVIDED_ATTRIBUTES.put(new Attribute("SLICE_COLOR", "getSliceColor", "setSliceColor", Pie.class, DEFAULT_SLICE_COLOR, Paint.class));
		PROVIDED_ATTRIBUTES.put(new Attribute("ANGLE", "getAngle", "setAngle", Pie.class, 0.0));
		PROVIDED_ATTRIBUTES.put(new Attribute("SIZE", "getSize", "setSize", Pie.class, 1.0));

		PROVIDED_ATTRIBUTES.remove(StandardAttribute.FILL_COLOR.name()); //Fill color is meaningless
	}


	protected double slice;
	protected double field;

	protected double angle;

	protected PPath sliceGlyph;

	public Pie(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);

		sliceGlyph = new PPath();
		sliceGlyph.setStrokePaint(null);
		super.path.addChild(sliceGlyph);
	}

	public double getPercent() {return slice/(field + slice);}
	public double getField() {return field;}
	public double getSlice() {return slice;}
	public double getAngle() {return angle;}
	public Paint getSliceColor() {return sliceGlyph.getPaint();}

	public void setPercent(double p) {
		field = 1-p;
		slice = p;
		verifyShape();
	}

	public void setField(double f) {field = f; verifyShape();}
	public void setSlice(double s) {slice = s; verifyShape();}
	public void setAngle(double a) {angle = a; verifyShape();}

	public void setSliceColor(Paint c) {
		if (c == null) {sliceGlyph.setPaint(DEFAULT_SLICE_COLOR);}
		else {sliceGlyph.setPaint(c);}
	}

	public Double getSize() {return Math.max(this.getWidth(), this.getHeight());}
	public void setSize(Double size) {
		assert (size >=0) : "Size cannot be negative";

		setWidth(size);
		setHeight(size);
	}

	private void verifyShape() {
		double angle = this.angle;
		double percent = getPercent();
		double x = getX();
		double y = getY();
		double size = getSize();
		double strokeWidth = (Double) getAttribute(StrokeProperty.STROKE_WEIGHT.name());
		Color strokePaint = (Color) getAttribute(StrokeProperty.STROKE_COLOR.name());
		
		java.awt.Shape arc = Pies.makeSlice(angle, percent, x, y, size, strokeWidth, strokePaint);
		java.awt.Shape outline = Pies.makePieOutline(angle, percent, x, y, size, strokeWidth, strokePaint);

		super.setPath(outline);
		if (arc != null) {sliceGlyph.setPathTo(arc);}
		else {sliceGlyph.setPathTo(new java.awt.geom.GeneralPath());}
	}
}
