/** Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
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
package stencil.adapters.piccoloDynamic;


import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;

import stencil.types.Converter;
import stencil.util.enums.EnumUtils;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**View tuple that supports zoom, width and height.
 * Zoom will be the zoom larger factor (between width or height)
 * if they are not equal.  Setting zoom will set both width and height.*/
public class ViewTuple extends stencil.display.ViewTuple.Simple {
	protected PCamera camera;

	public ViewTuple(PCamera camera) {
		this.camera = camera;
	}


	@SuppressWarnings("incomplete-switch")
	public void set(String field, Object value) {
		double val = Converter.toDouble(value);
		PAffineTransform viewTrans = camera.getViewTransformReference();

		double oldx, oldy, scalex, scaley, space;

		oldx = -(Double) get("X");
		oldy = -(Double) get("Y");
		scalex = viewTrans.getScaleX();
		scaley = viewTrans.getScaleY();

		if (EnumUtils.contains(ViewAttribute.class, field)) {
			ViewAttribute ename = ViewAttribute.valueOf(field);
			switch(ename){
				case ZOOM:
					viewTrans.setToScale(val, val);
					viewTrans.translate(oldx, oldy);
					return;
				case X:
					viewTrans.setToScale(scalex, scaley);
					viewTrans.translate(-val, oldy);
					return;
				case Y:
					viewTrans.setToScale(scalex, scaley);
					viewTrans.translate(oldx, -val);
					return;
				case WIDTH:
					space = camera.getWidth();
					viewTrans.setToScale(space/val, scaley);
					viewTrans.translate(oldx, oldy);
					return;
				case HEIGHT:
					space = camera.getHeight();
					viewTrans.setToScale(scalex,space/val);
					viewTrans.translate(oldx, oldy);
					return;
			}
		}
		throw new IllegalArgumentException(String.format("Cannot set %1$s on view.", field));
	}

	public Object get(String name) {
		PBounds viewBounds = camera.getViewBounds();
		PAffineTransform viewTrans = camera.getViewTransformReference();

		if (EnumUtils.contains(ViewAttribute.class, name)) {
			ViewAttribute ename = ViewAttribute.valueOf(name);
			switch (ename) {
				case ZOOM: return camera.getViewScale();
				case IMPLANTATION: return VIEW_IMPLANTATION;
				case X: return -viewTrans.getTranslateX()/viewTrans.getScaleX();
				case Y: return -viewTrans.getTranslateY()/viewTrans.getScaleY();
				case PORTAL_WIDTH: return camera.getWidth();
				case PORTAL_HEIGHT: return camera.getHeight();
				case WIDTH: return viewTrans.getScaleX() * viewBounds.getWidth();
				case HEIGHT: return viewTrans.getScaleY() * viewBounds.getHeight();
				default: throw new RuntimeException("Did not handle value in ViewAttribute enum: " + name);
			}
		}

		//TODO: Remove when math works better
		if (name.equals("RIGHT")) {return (Double) get("X") + (Double) get("WIDTH");}
		if (name.equals("BOTTOM")) {return (Double) get("Y") + (Double) get("HEIGHT");}
		if (name.equals("SCALE_X")) {return viewTrans.getScaleX();}
		if (name.equals("SCALE_Y")) {return viewTrans.getScaleY();}

		throw new IllegalArgumentException("Unknown field, cannot query " + name + " on view.");
	}

	public Object get(String name, Class<?> type) throws IllegalArgumentException {return Converter.convert(get(name), type);}


	public Point2D canvasToView(Point2D p) {return camera.globalToLocal(camera.localToView(p));}
	public Point2D viewToCanvas(Point2D p) {return camera.localToView(camera.globalToLocal(p));}
	public Dimension2D canvasToView(Dimension2D d) {return camera.globalToLocal(camera.localToView(d));}
	public Dimension2D viewToCanvas(Dimension2D p) {return camera.localToView(camera.globalToLocal(p));}
}
