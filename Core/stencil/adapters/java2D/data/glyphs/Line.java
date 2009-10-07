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


 import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

import stencil.streams.Tuple;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Strokes;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;

public final class Line extends Stroked {
	private static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);;
	private static final AttributeList UNSETTABLES = new AttributeList();
	private static final String IMPLANTATION = "LINE";
	
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> WIDTH = new Attribute("WIDTH", 0d);
	private static final Attribute<Double> HEIGHT = new Attribute("HEIGHT", 0d);
	private static final Attribute<Double> X1 = new Attribute("X.1", 0d);
	private static final Attribute<Double> X2 = new Attribute("X.2", 0d);
	private static final Attribute<Double> Y1 = new Attribute("Y.1", 0d);
	private static final Attribute<Double> Y2 = new Attribute("Y.2", 0d);

	static {
		ATTRIBUTES.add(X1);
		ATTRIBUTES.add(Y1);
		ATTRIBUTES.add(X2);
		ATTRIBUTES.add(Y2);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(WIDTH);
		ATTRIBUTES.add(HEIGHT);

		UNSETTABLES.add(X);
		UNSETTABLES.add(Y);
		UNSETTABLES.add(WIDTH);
		UNSETTABLES.add(HEIGHT);
	}

	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	private final java.awt.Shape glyph;
	private final java.awt.geom.Rectangle2D bounds;
	
	public Line(DisplayLayer layer, String id) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		x1 = X1.defaultValue;
		y1 = Y1.defaultValue;
		x2 = X2.defaultValue;
		y2 = Y1.defaultValue;	

		glyph = new Line2D.Double(x1,y1,x2,y2);		
		bounds = outlineStyle.createStrokedShape(glyph).getBounds2D();
	}
	
	
	
	private Line(String id, Line source) {
		super(id, source);
		this.x1 = source.x1;
		this.y1 = source.y1;
		this.x2 = source.x2;
		this.y2 = source.y2;
		this.glyph = source.glyph;
		this.bounds = source.bounds;
	}

	private Line(Line source, Tuple option) {
		super(source, option, UNSETTABLES);

		x1 = switchCopy(source.x1, safeGet(option, X1));
		x2 = switchCopy(source.x2, safeGet(option, X2));
		y1 = switchCopy(source.y1, safeGet(option, Y1));
		y2 = switchCopy(source.y2, safeGet(option, Y2));
		
		glyph = new Line2D.Double(x1,y1,x2,y2);		
		bounds = outlineStyle.createStrokedShape(glyph).getBounds2D();
	}
	
	public Rectangle2D getBoundsReference() {return bounds;}

	public String getImplantation() {return IMPLANTATION;} 
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	
	public Object get(String name) {
		if (X1.is(name)) 	  {return x1;}
		else if (Y1.is(name)) {return y1;}
		else if (X2.is(name)) {return x2;}
		else if (Y2.is(name)) {return y2;}
		else if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		else if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		else if (WIDTH.is(name)) {return bounds.getWidth();}
		else if (HEIGHT.is(name)) {return bounds.getHeight();}
		else{return super.get(name);}		
	}
		
	public void render(Graphics2D g, AffineTransform base) {
		if (bounds.getWidth() ==0 || bounds.getHeight() ==0) {return;}

		super.render(g,glyph);	
		super.postRender(g, base);
	}

	public Line update(Tuple t) throws IllegalArgumentException {return new Line(this, t);}
	public Line updateID(String id) {return new Line(id, this);}
}
