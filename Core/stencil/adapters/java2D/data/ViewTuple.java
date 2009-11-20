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
package stencil.adapters.java2D.data;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;
import stencil.tuple.InvalidNameException;
import stencil.types.Converter;
import stencil.util.DoubleDimension;
import stencil.util.enums.EnumUtils;

public final class ViewTuple extends stencil.display.ViewTuple.Simple {
	private Panel view;
	private Canvas canvas;
	public ViewTuple(Panel view) {
		this.view = view;
		canvas = view.getCanvas().getComponent();
	}

	@SuppressWarnings("incomplete-switch")
	public void set(String name, Object value) {
		if (EnumUtils.contains(ViewAttribute.class, name)) {
			ViewAttribute ename = ViewAttribute.valueOf(name);

			AffineTransform t = canvas.getViewTransform();
			double scaleY = t.getScaleY();
			double scaleX = t.getScaleX();

			double space;
			double val = Converter.toDouble(value);
			double oldY = -getY();
			double oldX = -getX();
			Point2D p = new Point2D.Double(t.getTranslateX(), t.getTranslateY());

			switch (ename) {
				case ZOOM:
					canvas.zoomTo(p, val);
					return;
		
				case X:
					t.setToScale(scaleX, scaleY);
					t.translate(-val, oldY);
					try {canvas.setViewTransform(t);}
					catch (NoninvertibleTransformException e) {/*Modifications to translate cannot give errors.*/}
					return;
					
				case Y: 
					t.setToScale(scaleX, scaleY);
					t.translate(oldX, -val);
					try {canvas.setViewTransform(t);}
					catch (NoninvertibleTransformException e) {/*Modifications to translate cannot give errors.*/}
					return;
					
				case WIDTH:
					space = canvas.getWidth();
					if (space == 0) {space =1;} //TODO: Is this the right thing to do?

					t.setToScale(space/val, scaleY);
					t.translate(oldX, oldY);

					try {canvas.setViewTransform(t);}
					catch (NoninvertibleTransformException e) {/*Scale so calculated cannot yield error.*/}

					return;
				case HEIGHT:
					space = canvas.getHeight();
					if (space == 0) {space =1;} //TODO: Is this the right thing to do?

					t.setToScale(scaleX, space/val);
					t.translate(oldX, oldY);

					try {canvas.setViewTransform(t);}
					catch (NoninvertibleTransformException e) {/*Scale so calculated cannot yield error.*/}
					return;
			}
		}
		throw new IllegalArgumentException(String.format("Cannot set %1$s on view.", name));
	}

	public Object get(String name) throws InvalidNameException {
		if (EnumUtils.contains(ViewAttribute.class, name)) {
			AffineTransform t = canvas.getInverseViewTransform();
			ViewAttribute ename = ViewAttribute.valueOf(name);
			Point2D p;
			
			switch (ename) {
				case ZOOM: return canvas.getScale();
				case IMPLANTATION: return VIEW_IMPLANTATION;
				case X: return getX();
				case Y: return getY();
				case PORTAL_WIDTH: return view.getBounds().getWidth();
				case PORTAL_HEIGHT: return view.getBounds().getHeight();
				case WIDTH: 
					p = new Point2D.Double(view.getBounds().getWidth(), 0);
					return t.deltaTransform(p,p).getX();
				case HEIGHT: 
					p = new Point2D.Double(0,view.getBounds().getHeight());
					return t.deltaTransform(p, p).getY();
				default: throw new RuntimeException("Did not handle value in ViewAttribute enum: " + name);
			}
		}

		//TODO: Remove when math works better
		if (name.equals("RIGHT")) {return (Double) get("X") + (Double) get("WIDTH");}
		if (name.equals("BOTTOM")) {return (Double) get("Y") + (Double) get("HEIGHT");}
		throw new IllegalArgumentException("Unknown field, cannot query " + name + " on view.");
	}
	
	private double getX() {
		AffineTransform t = canvas.getViewTransform();
		return -t.getTranslateX()/t.getScaleX();
	}
	
	private double getY() {
		AffineTransform t = canvas.getViewTransform();
		return -t.getTranslateY()/t.getScaleY();	
	}
	
	public Point2D canvasToView(Point2D p) {
		return canvas.getViewTransform().transform(p, p);
	}
	
	public Dimension2D canvasToView(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		canvas.getViewTransform().deltaTransform(p, p);
		d.setSize(p.getX(), p.getY());
		return d;
	}
	
	public Point2D viewToCanvas(Point2D p) {
		return canvas.getInverseViewTransform().transform(p, p);
	}
	
	public Dimension2D viewToCanvas(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		p = canvas.getInverseViewTransform().deltaTransform(p, p);
		return new DoubleDimension(p.getX(), p.getY());
	}
}
