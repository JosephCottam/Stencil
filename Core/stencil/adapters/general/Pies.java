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
package stencil.adapters.general;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import java.awt.Paint;
import java.awt.Shape;

public final class Pies {
	private Pies() {/*utility class.*/}

	private static double marginizedSize(double size, double strokeWidth, Paint strokePaint) {
		double marginWidth = strokeWidth;
		if (strokePaint instanceof Color && ((Color) strokePaint).getAlpha() == 1) {marginWidth=0;}
		return size-marginWidth;
	}

	private static double marginzedLocation(double l, double strokeWidth, Paint strokePaint) {
		double marginWidth = strokeWidth;
		if (strokePaint instanceof Color && ((Color) strokePaint).getAlpha() == 1) {marginWidth=0;}
		return l + (marginWidth/2.0);
	}
	
	
	public static Shape makePieOutline(double angle, double percent, double x, double y, double size, double strokeWidth, Paint strokePaint) {
		java.awt.Shape outline = null;
		outline = new Ellipse2D.Double();
		
		double drawSize = marginizedSize(size, strokeWidth, strokePaint);
		x = marginzedLocation(x, strokeWidth, strokePaint);
		y = marginzedLocation(y, strokeWidth, strokePaint);
	
		((Ellipse2D)outline).setFrame(x,y,drawSize, drawSize);
		return outline;
	}
	
	/**Return a shape to correspond to the filling.
	 * 
	 * If percent is 1, returns a circle.
	 * If 1>percent>0, returns an arc.
	 * If percent is zero, returns null.
	 **/
	public static Shape makeSlice(double angle, double percent, double x, double y, double size, double strokeWidth, Paint strokePaint) {
		if (percent ==0) {return null;}

		java.awt.Shape arc = null;

		Double circleSize=marginizedSize(size, strokeWidth, strokePaint);
		x = marginzedLocation(x, strokeWidth, strokePaint);
		y = marginzedLocation(y, strokeWidth, strokePaint);


		//Render appropriately
		if (percent != 0 && percent != 1) {
			Arc2D a;
			double startAngle;
			double extentAngle;

			startAngle = (90 + angle) %360;
			extentAngle = -360*percent;

			a = new Arc2D.Double(Arc2D.PIE);
			a.setAngleStart(startAngle);
			a.setAngleExtent(extentAngle);
			a.setFrame(x,y, circleSize, circleSize);
			arc =a;
		} else if (percent ==1) {
			arc = new Ellipse2D.Double();
			((Ellipse2D) arc).setFrame(x,y, circleSize, circleSize);
		}
	
		return arc;
	}
}
