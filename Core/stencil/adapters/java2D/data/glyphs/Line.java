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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.lang.ref.SoftReference;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Strokes;
import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.general.Shapes.StandardShape;
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
	private static final Attribute<String> CAP1 = new Attribute("CAP1", "NONE");
	private static final Attribute<String> CAP2 = new Attribute("CAP2", "NONE");
	private static final Attribute<String> SCALE_BY = new Attribute("SCALE_BY", "ALL");

	static {
		ATTRIBUTES.add(X1);
		ATTRIBUTES.add(Y1);
		ATTRIBUTES.add(X2);
		ATTRIBUTES.add(Y2);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(WIDTH);
		ATTRIBUTES.add(HEIGHT);
		ATTRIBUTES.add(CAP1);
		ATTRIBUTES.add(CAP2);
		ATTRIBUTES.add(SCALE_BY);

		UNSETTABLES.add(X);
		UNSETTABLES.add(Y);
		UNSETTABLES.add(WIDTH);
		UNSETTABLES.add(HEIGHT);
	}

	private final String scaleBy;
	
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	private final String cap1;
	private final String cap2;
	
	private final SoftReference<Line2D> glyph;
	
	public Line(DisplayLayer layer, String id) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		x1 = X1.defaultValue;
		y1 = Y1.defaultValue;
		x2 = X2.defaultValue;
		y2 = Y1.defaultValue;	

		cap1 = CAP1.defaultValue;
		cap2 = CAP2.defaultValue;
		
		scaleBy = SCALE_BY.defaultValue;
		
		Line2D l = makeLine();
		glyph = new SoftReference(l);		
		super.updateBoundsRef(outlineStyle.createStrokedShape(l).getBounds2D());
	}
	
	
	private Line(String id, Line source) {
		super(id, source);
		this.x1 = source.x1;
		this.y1 = source.y1;
		this.x2 = source.x2;
		this.y2 = source.y2;
		this.glyph = source.glyph;
		this.cap1 = source.cap1;
		this.cap2 = source.cap2;
		this.scaleBy = source.scaleBy;
	}

	private Line(Line source, Tuple option) {
		super(source, option, UNSETTABLES);

		x1 = switchCopy(source.x1, safeGet(option, X1));
		x2 = switchCopy(source.x2, safeGet(option, X2));
		y1 = switchCopy(source.y1, safeGet(option, Y1));
		y2 = switchCopy(source.y2, safeGet(option, Y2));
		
		cap1 = switchCopy(source.cap1, safeGet(option, CAP1));
		cap2 = switchCopy(source.cap1, safeGet(option, CAP2));
		
		scaleBy = switchCopy(source.scaleBy, safeGet(option, SCALE_BY));
		
		Line2D s = makeLine();
		glyph = new SoftReference(s);		
		super.updateBoundsRef(outlineStyle.createStrokedShape(s).getBounds2D());
	}

	public String getImplantation() {return IMPLANTATION;} 
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	private Line2D makeLine() {
		return new Line2D.Double(x1,y1,x2,y2);
	}
	
	public Object get(String name) {
		if (X1.is(name)) 	  {return x1;}
		else if (Y1.is(name)) {return y1;}
		else if (X2.is(name)) {return x2;}
		else if (Y2.is(name)) {return y2;}
		else if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		else if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		else if (WIDTH.is(name)) {return bounds.getWidth();}
		else if (HEIGHT.is(name)) {return bounds.getHeight();}
		else if (CAP1.is(name)) {return cap1;}
		else if (CAP2.is(name)) {return cap2;}
		else if (SCALE_BY.is(name)) {return scaleBy;}
		else if (SCALE_BY.is(name)) {return scaleBy;}
		else{return super.get(name);}		
	}

	
	/**Modify the graphcis 2D transform and passed line
	 * to suit the scale-by parameters.
	 * 
	 * @param g
	 * @param l
	 * @return
	 */
	private static final Line2D fixTransform(String scaleBy, Graphics2D g, Line2D l) {
		if (scaleBy.equals("ALL")) {return l;}
		
		if (scaleBy.equals("NONE")) {
			AffineTransform vt = g.getTransform();
			Point2D start = vt.transform(l.getP1(), null);
			Point2D end = vt.transform(l.getP2(), null);
			g.setTransform(IDENTITY_TRANSFORM);
			return new Line2D.Double(start, end);
		}
		
		throw new IllegalArgumentException("Can only scale line by ALL or NONE");
	}
	
	public void render(Graphics2D g, AffineTransform base) {
		if (bounds.getWidth() ==0 || bounds.getHeight() ==0) {return;}

		Line2D l = glyph.get();
		if (l == null) {l = makeLine();}
		
		Line2D toRender = fixTransform(scaleBy, g,l);
		
		super.render(g,toRender);
		endPoints(g, toRender, Converter.toDouble(super.get("STROKE_WEIGHT")), cap1, cap2);
		super.postRender(g, base);
	}

	private static void endPoints(Graphics2D g, Line2D l, double weight, String cap1, String cap2) {
		double x1 = l.getX1();
		double x2 = l.getX2();
		double y1 = l.getY1();
		double y2 = l.getY2();
		
		double scale = weight*6;
		double offset = scale/2.0;
		double slope = (y2-y1)/(x2-x1);
		double rotation;
		
		if (slope ==0) {rotation =0;}
		else if (slope > 0) {rotation = Math.atan(slope);}
		else {rotation = Math.atan(slope) + Math.PI;}
		
		if ((x1 >= x2 && y1 > y2)
			|| (x1 < x2 && y1 > y2)) {rotation = rotation + Math.PI;}
		
		if (cap1.equals("ARROW")) {
			double x = x1-offset;
			double y = y1-offset;
			if (slope ==0 && x1 > x2) {rotation =Math.PI;}
			Point2D pt = Registrations.topLeftToRegistration(Registration.CENTER, x,y,scale,scale);
			GeneralPath p = new GeneralPath(Shapes.getShape(StandardShape.TRIANGLE_LEFT, x, y, scale, scale));
			p.transform(AffineTransform.getRotateInstance(rotation, pt.getX(), pt.getY()));
			g.fill(p);
		}
		
		if (cap2.equals("ARROW")) {
			double x = x2-offset;
			double y = y2-offset;
			rotation = rotation + Math.PI;
			Point2D pt = Registrations.topLeftToRegistration(Registration.CENTER, x,y, scale, scale);
			GeneralPath p = new GeneralPath(Shapes.getShape(StandardShape.TRIANGLE_LEFT, x,y, scale, scale));
			p.transform(AffineTransform.getRotateInstance(rotation, pt.getX(), pt.getY()));
			g.fill(p);
		}
	}
	
	public Line update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}

		return new Line(this, t);
	}
	public Line updateID(String id) {return new Line(id, this);}
}
