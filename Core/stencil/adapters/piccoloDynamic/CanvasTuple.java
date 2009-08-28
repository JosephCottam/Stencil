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
package stencil.adapters.piccoloDynamic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import stencil.util.Tuples;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PBounds;


public class CanvasTuple extends stencil.display.CanvasTuple.SimpleCanvasTuple {
	protected PCanvas canvas;
	public CanvasTuple(PCanvas canvas) {
		this.canvas = canvas;
	}

	public PCanvas getPCanvas() {return canvas;}

	public void set(String field, Object value) {
		if (CanvasAttribute.BACKGROUND_COLOR.name().equals(field)) {
			canvas.setBackground((Color) Tuples.convert(value, Color.class));
		} else {
			throw new IllegalArgumentException("Cannot modify " + field + " on canvas.");
		}
	}
	
	/**Friendly method.  Assumes that the name you asked for will return a double.*/
	double getDouble(String name) {return (Double) get(name);} 

	public Component getComponent() {return canvas;}
	
	public Rectangle getBounds() {
		PBounds b = canvas.getCamera().getUnionOfLayerFullBounds();
		b.expandNearestIntegerDimensions();
		return b.getBounds();
	}

}
