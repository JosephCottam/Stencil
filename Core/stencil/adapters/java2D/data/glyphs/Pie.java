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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import stencil.adapters.general.Pies;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Strokes;
import stencil.adapters.general.Strokes.StrokeProperty;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.AttributeList;
import stencil.adapters.java2D.util.Attribute;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;

public final class Pie extends Stroked {
	private static final String IMPLANTATION = "PIE";
	
	private static final Color DEFAULT_SLICE_COLOR  = Color.RED;
	private static final Color DEFAULT_FIELD_COLOR  = Color.WHITE;

	private static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);;
	private static final AttributeList UNSETTABLES = new AttributeList();;

	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> PERCENT = new Attribute("PERCENT", 0.5d);
	private static final Attribute<Double> SLICE = new Attribute("SLICE", 0.5d);
	private static final Attribute<Double> FIELD = new Attribute("FIELD", 0.5d);
	private static final Attribute<Color> SLICE_COLOR = new Attribute("SLICE_COLOR", DEFAULT_SLICE_COLOR, Paint.class);
	private static final Attribute<Color> FIELD_COLOR = new Attribute("FIELD_COLOR", DEFAULT_FIELD_COLOR, Paint.class);
	private static final Attribute<Double> ANGLE = new Attribute("ANGLE", 0.0d);
	private static final Attribute<Double> SIZE = new Attribute("SIZE", 1.0d);
	
	
	static {
		ATTRIBUTES.add(PERCENT);
		ATTRIBUTES.add(SLICE);
		ATTRIBUTES.add(FIELD);
		ATTRIBUTES.add(SLICE_COLOR);
		ATTRIBUTES.add(ANGLE);
		ATTRIBUTES.add(SIZE);
		ATTRIBUTES.add(FIELD_COLOR);
	}

	private final double size;	
	private final Paint slicePaint;
	private final Paint fieldPaint;

	private final double angle;
	private final double field;
	private final double slice;
	
	private final java.awt.Shape arc;
	private final java.awt.Shape outline;
	
	
	public Pie(DisplayLayer layer, String id) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		
		size = SIZE.defaultValue;
		slicePaint = SLICE_COLOR.defaultValue;
		fieldPaint = FIELD_COLOR.defaultValue;
		angle = ANGLE.defaultValue;
		field = FIELD.defaultValue;
		slice = SLICE.defaultValue;

		arc = Pies.makeSlice(angle, getPercent(), X.defaultValue, Y.defaultValue, size, (Double) get(StrokeProperty.STROKE_WEIGHT.name()), outlinePaint);
		outline = Pies.makePieOutline(angle, getPercent(), X.defaultValue, Y.defaultValue, size, (Double) get(StrokeProperty.STROKE_WEIGHT.name()), outlinePaint);
		super.updateBoundsRef(outline.getBounds2D());
	}
	
	
	
	private Pie(String id, Pie source) {
		super(id, source);
		
		this.size = source.size;
		this.slicePaint = source.slicePaint;
		this.fieldPaint = source.fieldPaint;
		this.angle = source.angle;
		this.field = source.field;
		this.slice = source.slice;
		this.arc = source.arc;
		this.outline = source.outline;
	}



	private Pie(Pie source, Tuple option) {
		super(source, option, UNSETTABLES);
		
		size = switchCopy(source.size, safeGet(option, SIZE));
		slicePaint = switchCopy(source.slicePaint, safeGet(option, SLICE_COLOR));
		fieldPaint = switchCopy(source.fieldPaint, safeGet(option, FIELD_COLOR));
		angle = switchCopy(source.angle, safeGet(option, ANGLE));
		
		if (option.getPrototype().contains(PERCENT.name)) {
			slice = Converter.toDouble(option.get(PERCENT.name));
			field = 100 - slice;
		} else {
			field = switchCopy(source.field, safeGet(option, FIELD));
			slice = switchCopy(source.slice, safeGet(option, SLICE));
		}
		
		Point2D topLeft = mergeRegistrations(source, option, size, size, X, Y);
		
		arc = Pies.makeSlice(angle, getPercent(), topLeft.getX(), topLeft.getY(), size, (Double) get(StrokeProperty.STROKE_WEIGHT.name()), outlinePaint);
		outline = Pies.makePieOutline(angle, getPercent(), topLeft.getX(), topLeft.getY(), size, (Double) get(StrokeProperty.STROKE_WEIGHT.name()), outlinePaint);
		super.updateBoundsRef(outline.getBounds2D());
	}
	
	public Object get(String name) {
		if (PERCENT.is(name)) {return getPercent();} 
		if (SLICE.is(name)) {return slice;}
		if (FIELD.is(name)) {return field;}
		if (SLICE_COLOR.is(name)) {return slicePaint;}
		if (FIELD_COLOR.is(name)) {return fieldPaint;}
		if (ANGLE.is(name)) {return angle;}
		if (SIZE.is(name)) {return size;}
		if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		return super.get(name);
	}
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}


	private double getPercent() {return slice/(slice+field);}
	public String getImplantation() {return IMPLANTATION;}
	
	public void render(Graphics2D g, AffineTransform base) {
		g.setPaint(fieldPaint);
		g.fill(outline);
		
		if (arc != null) {
			g.setPaint(slicePaint);
			g.fill(arc);
		}

		super.render(g, outline);
		super.postRender(g, base);
	}

	public Pie update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}
		return new Pie(this, t);
	}
	public Pie updateID(String id) {return new Pie(id, this);}
}
