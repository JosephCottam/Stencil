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
package stencil.modules;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.display.CanvasTuple;

import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.parser.tree.Canvas;
import stencil.parser.tree.View;
import stencil.util.DoubleDimension;

public class ViewCanvas extends BasicModule {
	
	//Given an original registration and position, what would the X/Y be in the target registration
	public static double[] translateRegistration(Registration original, double x, double y, double width, double height, Registration target) {
		Point2D topLeft = Registrations.registrationToTopLeft(original, x, y, width, height);
		Point2D targetValue = Registrations.topLeftToRegistration(target, topLeft.getX(), topLeft.getY(), width, height);
		
		return new double[]{targetValue.getX(), targetValue.getY()};
	}
	
	
	public static double[] screenToCanvasPoint(double x, double y) {
		Point2D p = View.global.viewToCanvas(new Point2D.Double(x, y));
		return new double[]{p.getX(), p.getY()};
	}

	public static double[] screenToCanvasDimension(double width, double height) {
		Dimension2D p = View.global.viewToCanvas(new DoubleDimension( width, height));
		return new double[]{p.getWidth(), p.getHeight()};
	}


	public static double[] canvasToScreenPoint(double x, double y) {
		Point2D p = View.global.canvasToView(new Point2D.Double(x, y));
		return new double[]{p.getX(), p.getY()};
	}

	public static double[] canvasToScreenDimension(double width, double height) {
		Dimension2D p = View.global.canvasToView(new DoubleDimension(width, height));
		return new double[]{p.getWidth(), p.getHeight()};
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
	public static double[] zoom(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight) {
		return zoomPadded(portalWidth, portalHeight, canvasWidth, canvasHeight, 0);
	}
	
	/**Calculates a scale factor to keep values undistorted and all visible with a given amount of padding on all sides.
	 * Padding is specified in canvas pixels. 
	 * 
	 */
	public static double[] zoomPadded(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight, double pad) {
		CanvasTuple global = Canvas.global;
		
		double x = global.getX() - pad;
		double y = global.getY() - pad;
		double zy = canvasHeight !=0?portalHeight/canvasHeight:1;
		double zx = canvasWidth !=0?portalWidth/canvasWidth:1;
		double min = Math.min(zx, zy);
		if (min ==0 || Double.isInfinite(min) || Double.isNaN(min)) {min =1;}

		if (min == zx) {
			double newCanvasHeight = canvasHeight/min;
			double newPortalHeight = portalHeight/min;
			y = global.getY() + (newPortalHeight - newCanvasHeight)/2;
		} else {
			double newCanvasWidth = canvasWidth/min;
			double newPortalWidth = portalWidth/min;
			x = global.getX() - (newPortalWidth - newCanvasWidth)/2;
		}
			
		
		return new double[]{min, x, y, canvasWidth};
	}
	
	public ViewCanvas(ModuleData md) {super(md);}
}