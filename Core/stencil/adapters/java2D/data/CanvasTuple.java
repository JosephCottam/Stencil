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

import java.awt.Rectangle;
import java.awt.Color;
import stencil.adapters.java2D.Canvas;
import stencil.types.Converter;


public class CanvasTuple extends stencil.display.CanvasTuple.SimpleCanvasTuple {
	private Canvas  canvas;
	
	public CanvasTuple(Canvas c) {canvas = c;}
	
	public Canvas getComponent() {return canvas;}

	protected Rectangle getBounds() {return canvas.getContentBounds();}

	public void set(String field, Object value) {
		if (CanvasAttribute.BACKGROUND_COLOR.name().equals(field)) {
			canvas.setBackground((Color) Converter.convert(value, Color.class));
		} else {
			throw new IllegalArgumentException("Cannot modify " + field + " on canvas.");
		}
	}

	
	public boolean isDefault(String name, Object value) {
		if (CanvasAttribute.BACKGROUND_COLOR.name().equals(name)) {
			return CanvasAttribute.BACKGROUND_COLOR.getDefaultValue().equals(canvas.getBackground());
		} else if (CanvasAttribute.X.name().equals(name) ||
					CanvasAttribute.Y.name().equals(name)) {
			return (value instanceof Number && ((Number) value).intValue() ==0);
		}
		return super.isDefault(name, value);
	}
}
