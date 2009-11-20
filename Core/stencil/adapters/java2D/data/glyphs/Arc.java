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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Strokes;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public class Arc extends Stroked {
	protected static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES = new AttributeList();
	
	private static final String IMPLANTATION = "ARC";
	
	private static final Attribute<Double> X1 = new Attribute("X.1", 0d);
	private static final Attribute<Double> X2 = new Attribute("X.2", 0d);
	private static final Attribute<Double> Y1 = new Attribute("Y.1", 0d);
	private static final Attribute<Double> Y2 = new Attribute("Y.2", 0d);
	private static final Attribute<Double> ARC_HEIGHT = new Attribute("ARC_HEIGHT", 10.0);

	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> WIDTH = new Attribute("WIDTH", 0d);
	private static final Attribute<Double> HEIGHT = new Attribute("HEIGHT", 0d);

	
	static {
		ATTRIBUTES.add(X1);
		ATTRIBUTES.add(Y1);
		ATTRIBUTES.add(X2);
		ATTRIBUTES.add(Y2);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(ARC_HEIGHT);
		
		UNSETTABLES.add(X);
		UNSETTABLES.add(Y);
		UNSETTABLES.add(WIDTH);
		UNSETTABLES.add(HEIGHT);
	}

	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	private final double arcHeight;

	private final QuadCurve2D arc;
	
	public Arc(DisplayLayer layer, String id) {
		super(layer, id, Strokes.DEFAULT_STROKE, Strokes.DEFAULT_PAINT);
		
		x1 = X1.defaultValue;
		y1 = Y1.defaultValue;
		x2 = X2.defaultValue;
		y2 = Y2.defaultValue;
		arcHeight = ARC_HEIGHT.defaultValue;
		
		arc = validateArc();
		super.updateBoundsRef(arc.getBounds2D());
	}
	
	protected Arc(String id, Arc source) {
		super(id, source);
		
		this.x1 = source.x1;
		this.y1 = source.y1;
		this.x2 = source.x2;
		this.y2 = source.y2;
		this.arcHeight = source.arcHeight;
		this.arc = source.arc;
	}

	private Arc(Arc source, Tuple option) {
		super(source, option, UNSETTABLES);

		x1 = switchCopy(source.x1, safeGet(option, X1));
		x2 = switchCopy(source.x2, safeGet(option, X2));
		y1 = switchCopy(source.y1, safeGet(option, Y1));
		y2 = switchCopy(source.y2, safeGet(option, Y2));
		arcHeight = switchCopy(source.arcHeight, safeGet(option, ARC_HEIGHT));
		
		arc = validateArc();
		super.updateBoundsRef(arc.getBounds2D());
	}
		
	public String getImplantation() {return IMPLANTATION;} 

	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}

	public Object get(String name) {
		if (X1.is(name)) 	  {return x1;}
		else if (Y1.is(name)) {return y1;}
		else if (X2.is(name)) {return x2;}
		else if (Y2.is(name)) {return y2;}
		else if (ARC_HEIGHT.is(name)) {return arcHeight;}
		else if (X.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getX();}
		else if (Y.is(name)) {return Registrations.topLeftToRegistration(registration, bounds).getY();}
		else if (WIDTH.is(name)) {return bounds.getWidth();}
		else if (HEIGHT.is(name)) {return bounds.getHeight();}
		else{return super.get(name);}		
	}
	
	@Override
	public void render(Graphics2D g, AffineTransform base) {
		super.render(g, arc);
		super.postRender(g, null);
	}

	private QuadCurve2D validateArc() {
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p2 = new Point2D.Double(x2, y2);
		QuadCurve2D arc = new QuadCurve2D.Double();
		arc.setCurve(p1, getControl(p1, p2, arcHeight), p2);
		return arc;
	}
	
	
	private static final Point2D getControl(Point2D p1, Point2D p2, double arcHeight) {
		Point2D mid = midPoint(p1,p2);
		Point2D control;
		
		//Vertical line
		if (p1.getX() == p2.getX()) {
			control = new Point2D.Double(mid.getX()+arcHeight, mid.getY());

		//Horizontal line
		} else if (p1.getY() == p2.getY()) {
			control = new Point2D.Double(mid.getX(), mid.getY() + arcHeight);

		//Other types of lines
		} else {
			double length = p1.distance(p2);
			double scale = arcHeight/length;
			
			double rise = (p1.getY() - p2.getY()) * scale;
			double run =  (p1.getX() - p2.getX()) * scale;
			
			double x = mid.getX() - rise; 
			double y = mid.getY() + run;
				
			control = new Point2D.Double(x,y);
		}
		return control;
	}
	
	
	private static final Point2D midPoint(Point2D p1, Point2D p2) {
		double x = (p1.getX()+p2.getX())/2.0;
		double y = (p1.getY()+p2.getY())/2.0;
		return new Point2D.Double(x, y);
	}

	public Arc update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}

		return new Arc(this, t);
	}
	public Arc updateID(String id) {return new Arc(id, this);}
}
