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

import static stencil.adapters.GlyphAttributes.StandardAttribute.X;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.types.Converter;

public class Arc extends Stroked {
	protected static final AttributeList attributes;
	protected static final Attribute X1 = new Attribute("X.1", 0, double.class);
	protected static final Attribute X2 = new Attribute("X.2", 0, double.class);
	protected static final Attribute Y1 = new Attribute("Y.1", 0, double.class);
	protected static final Attribute Y2 = new Attribute("Y.2", 0, double.class);
	protected static final Attribute ARC_HEIGHT = new Attribute("ARC_HEIGHT", 10.0, double.class);
	
	static {
		attributes = new AttributeList(Stroked.attributes);

		attributes.add(X1);
		attributes.add(Y1);
		attributes.add(X2);
		attributes.add(Y2);
		attributes.add(ARC_HEIGHT);
		
		attributes.remove(StandardAttribute.HEIGHT);
		attributes.remove(StandardAttribute.WIDTH);
	}

	private double x1 = (Double) attributes.get(X.name()).defaultValue;
	private double y1 = (Double) attributes.get(X.name()).defaultValue;
	private double x2 = (Double) attributes.get(X.name()).defaultValue;
	private double y2 = (Double) attributes.get(X.name()).defaultValue;

	private QuadCurve2D arc = new QuadCurve2D.Double();
	private double arcHeight = (Double) ARC_HEIGHT.defaultValue;
	
	public Arc(String id) {super(id);}
	
	public double getHeight() {
		validateArc();
		return arc.getBounds2D().getHeight();
	}

	public double getWidth() {
		validateArc();
		return arc.getBounds2D().getWidth();
	}
	
	public String getImplantation() {return "LINE";} 

	protected AttributeList getAttributes() {return attributes;}
	
	public Object get(String name) {
		if (X1.is(name)) 	  {return x1;}
		else if (Y1.is(name)) {return y1;}
		else if (X2.is(name)) {return x2;}
		else if (Y2.is(name)) {return y2;}
		else if (ARC_HEIGHT.is(name)) {return arcHeight;}
		else{return super.get(name);}		
	}
	
	public void set(String name, Object value) {
		if (X1.is(name)) 	  {x1 = Converter.toDouble(value); validate();}
		else if (Y1.is(name)) {y1 = Converter.toDouble(value); validate();}
		else if (X2.is(name)) {x2 = Converter.toDouble(value); validate();}
		else if (Y2.is(name)) {y2 = Converter.toDouble(value); validate();}
		else if (name.equals("X") || name.equals("Y")) {throw new IllegalArgumentException("Cannot set raw X and Y on a line.");}
		else if (ARC_HEIGHT.is(name)) {arcHeight = Converter.toDouble(value);}
		else{super.set(name, value);}
	}

	private void validate() {
		double x,y;
		x = Math.min(x1,x2);
		y = Math.min(y1,y2);
		
		Point2D p = Registrations.topLeftToRegistration(registration, x,y,getWidth(), getHeight());
		this.x = p.getX();
		this.y = p.getY();
		
		arc = null;
	}
	
	@Override
	public void render(Graphics2D g, AffineTransform base) {
		QuadCurve2D arc = this.arc;
		if (arc == null) {arc = validateArc();}
		
		super.render(g, arc);
		super.postRender(g, null);
	}
	
	public void setArcHeight(double arcHeight) {
		if (this.arcHeight == arcHeight) {return;}
		this.arcHeight = arcHeight;
		arc = null;
	}
	
	private QuadCurve2D validateArc() {
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p2 = new Point2D.Double(x2, y2);
		QuadCurve2D arc = new QuadCurve2D.Double();
		arc.setCurve(p1, getControl(p1, p2, arcHeight), p2);
		this.arc = arc;
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

}
