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
package stencil.operator.module.provided;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.display.CanvasTuple;

import stencil.operator.module.*;
import stencil.operator.module.util.BasicModule;
import stencil.parser.tree.Canvas;
import stencil.parser.tree.View;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.DoubleDimension;

public class Transform extends BasicModule {
	
	//Given an original registration and position, what would the X/Y be in the target registration
	public static Tuple translateRegistration(Object original, Object x, Object y, Object width, Object height, Object target) {
		x = Converter.toDouble(x);
		y = Converter.toDouble(y);
		width = Converter.toDouble(width);
		height = Converter.toDouble(height);
		original = Converter.convert(original, Registration.class);
		target = Converter.convert(target, Registration.class);
		
		
		Point2D topLeft = Registrations.registrationToTopLeft((Registration) original, (Double) x, (Double) y, (Double) width, (Double) height);
		Point2D targetValue = Registrations.topLeftToRegistration((Registration) target, topLeft.getX(), topLeft.getY(), (Double) width, (Double) height);
		
		String[] names = new String[]{"X", "Y"};
		Double[] values = new Double[]{targetValue.getX(), targetValue.getY()};
		return new PrototypedTuple(names, values);
	}
	
	
	public static Tuple screenToCanvasPoint(Object x, Object y) {
		x = Converter.toDouble(x);
		y = Converter.toDouble(y);
		Point2D p = View.global.viewToCanvas(new Point2D.Double((Double)x,(Double)y));
		return new PrototypedTuple(new String[]{"X","Y"}, new Double[]{p.getX(), p.getY()});
	}

	public static Tuple screenToCanvasDimension(Object width, Object height) {
		width = Converter.toDouble(width);
		height = Converter.toDouble(height);
		Dimension2D p = View.global.viewToCanvas(new DoubleDimension((Double)width,(Double)height));
		return new PrototypedTuple(new String[]{"Width","Height"}, new Double[]{p.getWidth(), p.getHeight()});
	}


	public static Tuple canvasToScreenPoint(Object x, Object y) {
		x = Converter.toDouble(x);
		y = Converter.toDouble(y);
		Point2D p = View.global.canvasToView(new Point2D.Double((Double)x,(Double)y));
		return new PrototypedTuple(new String[]{"X","Y"}, new Double[]{p.getX(), p.getY()});
	}

	public static Tuple canvasToScreenDimension(Object width, Object height) {
		width = Converter.toDouble(width);
		height = Converter.toDouble(height);
		Dimension2D p = View.global.canvasToView(new DoubleDimension((Double)width,(Double)height));
		return new PrototypedTuple(new String[]{"Width","Height"}, new Double[]{p.getWidth(), p.getHeight()});
	}

	/**Calculates the scale factor to keep values undistorted but all objects visible.
	 * If an illegal scale value appears (such as 0, NaN or Inf), the scale value returned is 1.
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param canvasWidth
	 * @param canvasHeight
	 * @return
	 */
	public static Tuple zoom(Object portalWidth, Object portalHeight, Object canvasWidth, Object canvasHeight) {
		return zoomPadded(portalWidth, portalHeight, canvasWidth, canvasHeight, 0);
	}
	
	/**Calculates a scale factor to keep values undistorted and all visible with a given amount of padding on all sides.
	 * Padding is specified in canvas pixels. 
	 * 
	 */
	public static Tuple zoomPadded(Object portalWidth, Object portalHeight, Object canvasWidth, Object canvasHeight, Object pad) {
		CanvasTuple global = Canvas.global;

		double p = Converter.toDouble(pad);
		double pw = Converter.toDouble(portalWidth);
		double ph = Converter.toDouble(portalHeight);
		double cw = Converter.toDouble(canvasWidth) + 2 * p;
		double ch = Converter.toDouble(canvasHeight) + 2 * p;
		double x = global.getX() - p;
		double y = global.getY() - p;
		
		double zy = ch !=0?ph/ch:1;
		double zx = cw !=0?pw/cw:1;
		double min = Math.min(zx, zy);
		if (min ==0 || Double.isInfinite(min) || Double.isNaN(min)) {min =1;}

		if (min == zx) {
			double newCanvasHeight = Converter.toDouble(canvasHeight)/min;
			double newPortalHeight = Converter.toDouble(portalHeight)/min;
			y = global.getY() + (newPortalHeight - newCanvasHeight)/2;
		} else {
			double newCanvasWidth = Converter.toDouble(canvasWidth)/min;
			double newPortalWidth = Converter.toDouble(portalWidth)/min;
			x = global.getX() - (newPortalWidth - newCanvasWidth)/2;
		}
			
		
		String[] names = new String[]{"zoom", "X", "Y", "width"};
		Tuple t = new PrototypedTuple(names, new Object[]{min, x, y, cw});
		return t;
	}
	
	public Transform(ModuleData md) {super(md);}
}