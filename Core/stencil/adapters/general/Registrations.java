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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;

/**Utilities for handling registration issues.  All methods ALWAYS return new points, even
 * if no correction is required.
 *
 * @author jcottam
 *
 */

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Registrations {
	/**Full list of allowed registrations.*/
	public static enum Registration {TOP_LEFT, TOP, TOP_RIGHT, LEFT, CENTER , RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT}


	private Registrations() {/*Utility class.  Not instantiable.*/}

	public static Point2D registrationToTopLeft(Registration registration, Point2D p, Dimension2D d) {
		return registrationToTopLeft(registration, p.getX(), p.getY(), d.getHeight(), d.getWidth());
	}

	public static Point2D registrationToTopLeft(Registration registration, Point2D p, double h, double w) {
		return registrationToTopLeft(registration, p.getX(), p.getY(), h, w);
	}
	public static Point2D registrationToTopLeft(Registration registration, double x, double y, double h, double w) {
		switch(registration) {
			case TOP_LEFT: 		return new Point2D.Double(x, 	 y);
			case TOP:			return new Point2D.Double(x-w/2, y);
			case TOP_RIGHT:		return new Point2D.Double(x-w, 	 y);
			case LEFT:			return new Point2D.Double(x, 	 y-h/2);
			case CENTER: 		return new Point2D.Double(x-w/2, y-h/2);
			case RIGHT:			return new Point2D.Double(x-w, 	 y-h/2);
			case BOTTOM_LEFT:	return new Point2D.Double(x, 	 y-h);
			case BOTTOM: 		return new Point2D.Double(x-w/2, y-h);
			case BOTTOM_RIGHT:	return new Point2D.Double(x-w, 	 y-h);
		}
		throw new IllegalArgumentException("Could not correct for registration: " + registration.name());
	}



	/**Convert the top-left corner of the given rectangle to
	 * the point indicated by the registration argument.
	 */
	public static Point2D topLeftToRegistration(Registration registration, Rectangle2D source) {
		switch(registration) {
			case TOP_LEFT: 		return new Point2D.Double(source.getMinX(),		source.getMinY());
			case TOP: 			return new Point2D.Double(source.getCenterX(), 	source.getMinY());
			case TOP_RIGHT: 	return new Point2D.Double(source.getMaxX(), 	source.getMinY());
			case LEFT: 			return new Point2D.Double(source.getMinX(),		source.getCenterY());
			case CENTER: 		return new Point2D.Double(source.getCenterX(),	source.getCenterY());
			case RIGHT:  		return new Point2D.Double(source.getMaxX(),		source.getCenterY());
			case BOTTOM_LEFT:	return new Point2D.Double(source.getMinX(),		source.getMaxY());
			case BOTTOM:		return new Point2D.Double(source.getCenterX(),	source.getMaxY());
			case BOTTOM_RIGHT:	return new Point2D.Double(source.getMaxX(),		source.getMaxY());
		}
		throw new IllegalArgumentException("Could not correct for registration: " + registration.name());
	}

	/**Assuming the point represents the top-left of a rectangle with width/height specified,
	 * give the relative point indicated by registration.
	 */
	public static Point2D topLeftToRegistration(Registration registration, Point2D source, double h, double w) {
		Rectangle2D r = new Rectangle2D.Double(source.getX(), source.getY(), h,w);
		return topLeftToRegistration(registration, r);
	}

	/**Assuming x and y indicate a top-left corner of a rectangle with width/height specified,
	 * give the relative point indicated by registration.
	 */
	public static Point2D topLeftToRegistration(Registration registration, double x, double y, double w, double h) {
		Rectangle2D r = new Rectangle2D.Double(x, y, h,w);
		return topLeftToRegistration(registration, r);
	}


}
