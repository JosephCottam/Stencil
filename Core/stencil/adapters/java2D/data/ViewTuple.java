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
import java.awt.geom.Point2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;
import stencil.streams.InvalidNameException;
import stencil.types.Converter;
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

			double oldY = t.getTranslateY();
			Point2D p = new Point2D.Double(t.getTranslateX(), t.getTranslateY());

			switch (ename) {
				case ZOOM:
					double delta = Converter.toDouble(value)/t.getScaleX();
					canvas.zoom(p, delta);
					return;
		
				case X:
					t.setToScale(scaleX, scaleY);
					t.translate(-Converter.toDouble(value), -oldY);
					try {canvas.setViewTransform(t);}
					catch (Exception e) {}
					return;
					
				case Y: 
//					p.setLocation(p.getX(), Converter.toDouble(value));
//					p = Registrations.topLeftToRegistration(Registration.CENTER, p, canvas.getContentBounds().getWidth(), canvas.getContentBounds().getHeight());
//					canvas.panTo(p);
					return;
					
				case WIDTH:
//					scaleX =  (Converter.toDouble(value)/view.getWidth())/scaleX;
//					p = canvas.getAbsoluteCoordinate(p, p);
//					canvas.zoomAbs(p, scaleX, scaleY);
					return;
				case HEIGHT:
//					scaleY =  (Converter.toDouble(value)/view.getHeight())/scaleY;
//					p = canvas.getAbsoluteCoordinate(p, p);
//					canvas.zoomAbs(p, scaleX, scaleY);
					return;
			}
		}
		throw new IllegalArgumentException(String.format("Cannot set %1$s on view.", name));
	}

	public Object get(String name) throws InvalidNameException {
		if (EnumUtils.contains(ViewAttribute.class, name)) {
			AffineTransform t = canvas.getViewTransform();
			ViewAttribute ename = ViewAttribute.valueOf(name);
			switch (ename) {
				case ZOOM: return canvas.getScale();
				case IMPLANTATION: return VIEW_IMPLANTATION;
				case X: return t.getTranslateX()/t.getScaleX();
				case Y: return t.getTranslateY()/t.getScaleY();
				case WIDTH: return t.getScaleX() * view.getBounds().getWidth();
				case HEIGHT: return t.getScaleY() * view.getBounds().getHeight();
				default: throw new RuntimeException("Did not handle value in ViewAttribute enum: " + name);
			}
		}

		//TODO: Remove when math works better
		if (name.equals("RIGHT")) {return (Double) get("X") + (Double) get("WIDTH");}
		if (name.equals("BOTTOM")) {return (Double) get("Y") + (Double) get("HEIGHT");}
		throw new IllegalArgumentException("Unknown field, cannot query " + name + " on view.");
	}
	
	public Point2D canvasToView(Point2D p) {
		return canvas.getViewTransform().transform(p, p);
	}
	
	public Dimension2D canvasToView(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		canvas.getViewTransform().transform(p, p);
		d.setSize(p.getX(), p.getY());
		return d;
	}
	
	public Point2D viewToCanvas(Point2D p) {
		return canvas.getAbsoluteCoordinate(p, p);
	}
	
	public Dimension2D viewToCanvas(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		canvas.getAbsoluteCoordinate(p, p);
		d.setSize(p.getX(), p.getY());
		return d;
	}
}
