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


import java.awt.geom.*;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.ImplicitArgumentException;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class Arc extends Path {
	public static final String IMPLANTATION_NAME = "ARC";
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : Path.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Xn, "getXArray", "setXArray", Arc.class, true, null, Double.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Yn, "getYArray", "setYArray", Arc.class, true, null, Double.class));
		PROVIDED_ATTRIBUTES.put(new Attribute("ARC_HEIGHT", "getArcHeight", "setArcHeight", Arc.class, false, new Double(10), Double.class));

		//TODO: Add full control for control point, not just height at center
		//TODO: Add, with respect to registration
//		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.WIDTH, "getWidth", "setWidth", Line.class, new Double(0)));
//		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.HEIGHT, "getHeight", "setHeight", Line.class, new Double(0)));
	}

	protected QuadCurve2D arc = new QuadCurve2D.Double();
	protected double arcHeight;
	
	public Arc(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);
		applyDefaults();
		super.setPath(arc);
	}

	/**Either move the line (attribute Y) or move the line end (Y1 or Y2)*/
	public void setYArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";

		int arg = index(att);
		Point2D p1 = arc.getP1();
		Point2D p2 = arc.getP2();

		switch(arg) {
			case 1: 
				if (value == p1.getY()) {return;}
				p1  = new Point2D.Double(p1.getX(), value);
				break;
			case 2:
				if (value == p2.getY()) {return;}
				p2  = new Point2D.Double(p2.getX(), value);
				break;
			default: throw new ImplicitArgumentException(Arc.class, baseName(att), arg);
		}
		
		arc.setCurve(p1, getControl(p1,p2, arcHeight), p2);

		super.setPath(arc);
	}

	public Object getYArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple Y in indexed Y accessor";

		int arg = index(att);

		switch(arg) {
			case 1: return arc.getY1();
			case 2: return arc.getY2();
			case -1: return new Double[]{arc.getY1(), arc.getY2()};
			default: throw new ImplicitArgumentException(Arc.class, baseName(att), arg);
		}
	}

	/**Either move the line (attribute X) or move the line end (X1 or X2)*/
	public void setXArray(String att, double value) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";

		int arg = index(att);
		Point2D p1 = arc.getP1();
		Point2D p2 = arc.getP2();
		
		switch(arg) {
			case 1: 
				if (value == p1.getX()) {return;}
				p1 = new Point2D.Double(value, p1.getY());
				break;
			case 2:
				if (value == p2.getX()) {return;}
				p2  = new Point2D.Double(value, p2.getY());
				break;
			default: throw new ImplicitArgumentException(Arc.class, baseName(att), arg);
		}
		arc.setCurve(p1, getControl(p1,p2, arcHeight), p2);

		super.setPath(arc);
	}

	public Object getXArray(String att) throws ImplicitArgumentException {
		assert !att.equals(StandardAttribute.Y.name()) : "Recieved simple X in indexed X accessor";

		int arg = index(att);

		switch(arg) {
			case 1: return arc.getX1();
			case 2: return arc.getX2();
			case -1: return new Double[]{arc.getX1(), arc.getX2()};
			default: throw new ImplicitArgumentException(Arc.class, baseName(att), arg);
		}
	}
	
	public void setArcHeight(double arcHeight) {
		if (this.arcHeight == arcHeight) {return;}
		this.arcHeight = arcHeight;
		
		arc.setCurve(arc.getP1(), getControl(arc.getP1(),arc.getP2(), arcHeight), arc.getP2());
	}

	public double getArcHeight() {return arcHeight;}

	
	private static final Point2D getControl(Point2D p1, Point2D p2, double arcHeight) {
		Point2D mid = midPoint(p1,p2);
		Point2D control;
		
		//Vertical line
		if (p1.getX() == p2.getX()) {
			control = new Point2D.Double(mid.getX()+arcHeight, mid.getY());

			//Horizontal line
		} else if (p1.getY() == p2.getY()) {
//			System.out.println(arc.toString() + "::" + arc.getP1() + " & " + arc.getP2()+ ":" + midPoint());
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

	/**What is the X/Y implicit argument (as a number)*/
	private int index(String att) {
		try {
			String arg = nameArgs(att);
			if (arg == null) {return -1;}
			int val = Integer.parseInt(arg);
			return val;
		} catch (Exception e) {throw new ImplicitArgumentException(Arc.class, att, e);}
	}

	public PBounds getBoundsReference() {return path.getBoundsReference();}

	
	protected void paint(PPaintContext paintContext) {
		super.paint(paintContext);

		//If the bounding box is being displayed, paint the control points
		if (Node.BOUNDING_BOX_COLOR != null) {
			paintContext.getGraphics().setPaint(Node.BOUNDING_BOX_COLOR.darker());
			Point2D[] points = new Point2D[] {arc.getP1(), arc.getP2(), arc.getCtrlPt()};
			double scale = 2;
			for (Point2D p: points) {
				p.setLocation(p.getX()-scale/2, p.getY()-scale/2);
				java.awt.Shape s = Shapes.getShape(StandardShape.ELLIPSE, new java.awt.geom.Rectangle2D.Double(p.getX(), p.getY(), scale,scale));				
				paintContext.getGraphics().draw(s);
			}
	
		}
	}
}
