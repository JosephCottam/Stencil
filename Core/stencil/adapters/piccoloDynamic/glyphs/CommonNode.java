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

import java.awt.geom.Point2D;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;
import stencil.types.Converter;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

public abstract class CommonNode extends Node {
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : Node.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.REGISTRATION, "getRegistration", "setRegistration", CommonNode.class, Registration.TOP_LEFT));

		//Modify to use registration-respecting X/Y coords
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.X.name(), "getXPosition", "setXPosition", CommonNode.class, 0.0));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Y.name(), "getYPosition", "setYPosition", CommonNode.class, 0.0));

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.WIDTH, "getWidth", "setWidth", CommonNode.class, new Double(1)));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.HEIGHT, "getHeight", "setHeight", CommonNode.class, new Double(1)));

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.ROTATION, "getRotate", "setRotate", CommonNode.class, 0.0));
	}

	protected Registration registration = (Registration) PROVIDED_ATTRIBUTES.get(StandardAttribute.REGISTRATION).defaultValue;
	protected PNode child;

	public CommonNode(String id, String implantation, Attributes att) {super(id, implantation, att);}

	public void setChild(PNode newChild) {
		if (child != null) {
			this.removeChild(child);
		}
		child = newChild;
		this.addChild(newChild);
		child.setPickable(false);
	}

	/**Find the rotation in degrees. Due to round-off error, results are only accurate to the nearest 1000th of a degree.*/
	public double getRotate() {
		double degrees = Math.toDegrees(child.getRotation());
		degrees = degrees * 1000;
		degrees = Math.round(degrees);  //JDK Says this conversion is often inexact, so we do some rounding.  Comes out good...most of the time.
		degrees = degrees/1000;
		if (degrees == 360d) {degrees = 0d;}
		return degrees;
	}

	/**Set the rotation in degrees.*/
	public void setRotate(double value) {
		Double degrees = Converter.toDouble(value);
		if (degrees == 360d) {degrees = 0d;}
		child.setRotation(Math.toRadians(degrees));
	}


	public Registration getRegistration() {return registration;}
	public void setRegistration(Registration registration) {
		if (registration == this.registration) {return;}

		this.registration = registration;
		//TODO: This seems sort of fake...is it correct?
		this.setXPosition(getXTranslate());
		this.setYPosition(getYTranslate());
	}

	public void setPosition(Point2D p) {
		this.setXPosition(p.getX());
		this.setYPosition(p.getY());
	}

	/**Wraps getX, respecting the registration.*/
	public final double getXPosition() {
		PBounds bounds = getGlobalFullBounds();
		if (bounds.isEmpty()) {return this.getXTranslate();}
		return Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight()).getX();
	}

	/**Wraps getY, respecting the registration.*/
	public final double getYPosition() {
		PBounds bounds = getGlobalFullBounds();
		if (bounds.isEmpty()) {return this.getYTranslate();}
		return Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight()).getY();
	}

	/**Wraps setX, respecting the registration.**/
	public void setXPosition(double x) {
		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());
		if (x == regPoint.getX()) {return;}

		x = this.getXTranslate() + (x - regPoint.getX());
		this.setXTranslate(x);
	}

	/**Warps setY, respecting the registration*/
	public void setYPosition(double y) {
		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());
		if (y == regPoint.getY()) {return;}
		y = this.getYTranslate() + (y - regPoint.getY());
		this.setYTranslate(y);
	}

	protected void paint(PPaintContext paintContext) {
		super.paint(paintContext);

		//If the bounding box is being displayed, paint registration point as a cross in a related color
		if (Node.BOUNDING_BOX_COLOR != null) {
			paintContext.getGraphics().setPaint(Node.BOUNDING_BOX_COLOR.darker());
			Point2D p =  Registrations.topLeftToRegistration(registration, getBoundsReference());
			double scale = 2;
			p.setLocation(p.getX()-scale/2, p.getY()-scale/2);
			java.awt.Shape s = Shapes.getShape(StandardShape.CROSS, new java.awt.geom.Rectangle2D.Double(p.getX(), p.getY(), scale,scale));
			paintContext.getGraphics().fill(s);
		}
	}

	public double getXTranslate() {return getTransformReference(true).getTranslateX();}
	public double getYTranslate() {return getTransformReference(true).getTranslateY();}

	public void setXTranslate(double x) {
		PAffineTransform trans = getTransformReference(true);
		double delta = x-trans.getTranslateX();
		translate(delta, 0);
		signalBoundsChanged();
	}

	public void setYTranslate(double y) {
		PAffineTransform trans = getTransformReference(true);
		double delta = y-trans.getTranslateY();
		translate(0, delta);
		signalBoundsChanged();
	}

	public boolean setHeight(double height) {
		if (height == super.getHeight()) {return false;}

		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());
		super.setHeight(height);
		setPosition(regPoint);
		signalBoundsChanged();
		return true;
	}

	public boolean setWidth(double width) {
		if (width == super.getWidth()) {return false;}

		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());
		super.setWidth(width);
		setPosition(regPoint);
		signalBoundsChanged();
		return true;
	}
}
