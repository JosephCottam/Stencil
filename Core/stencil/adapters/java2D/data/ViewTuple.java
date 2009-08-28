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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import stencil.adapters.java2D.Panel;
import stencil.streams.InvalidNameException;
import stencil.util.enums.EnumUtils;

public final class ViewTuple extends stencil.display.ViewTuple.Simple {
	private Panel view;
	
	public ViewTuple(Panel view) {this.view = view;}

	public void set(String field, Object value) {
		throw new UnsupportedOperationException("View is current read-only.");
	}

	public Object get(String name) throws InvalidNameException {
		if (EnumUtils.contains(ViewAttribute.class, name)) {
			ViewAttribute ename = ViewAttribute.valueOf(name);
			switch (ename) {
				case ZOOM: return view.getVewBounds().getWidth()/(Double)view.getCanvas().get("WIDTH");
				case IMPLANTATION: return VIEW_IMPLANTATION;
				case X: return view.getVewBounds().getX();
				case Y: return view.getVewBounds().getY();
				case WIDTH: return view.getVewBounds().getWidth();
				case HEIGHT: return view.getVewBounds().getHeight();
				default: throw new RuntimeException("Did not handle value in ViewAttribute enum: " + name);
			}
		}

		//TODO: Remove when math works better
		if (name.equals("RIGHT")) {return (Double) get("X") + (Double) get("WIDTH");}
		if (name.equals("BOTTOM")) {return (Double) get("Y") + (Double) get("HEIGHT");}
		throw new IllegalArgumentException("Unknown field, cannot query " + name + " on view.");
	}
	
	public Point2D canvasToView(Point2D p) {throw new UnsupportedOperationException("Not implemented.");}
	public Dimension2D canvasToView(Dimension2D p) {throw new UnsupportedOperationException("Not implemented.");}
	public Point2D viewToCanvas(Point2D p) {throw new UnsupportedOperationException("Not implemented.");}
	public Dimension2D viewToCanvas(Dimension2D p) {throw new UnsupportedOperationException("Not implemented.");}
}
