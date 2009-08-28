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
package stencil.adapters.java2D.data.glyphs;

import static stencil.util.enums.EnumUtils.contains;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import stencil.adapters.general.Strokes;
import stencil.adapters.general.Strokes.StrokeProperty;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;

public abstract class Stroked extends Point {
	protected static final AttributeList attributes;
	static {
		attributes = new AttributeList(Point.attributes);
		for (StrokeProperty p: StrokeProperty.values()) {attributes.add(new Attribute(p));}
	}

	protected Stroke outlineStyle = Strokes.DEFAULT_STROKE;
	protected Paint outlinePaint = Strokes.DEFAULT_PAINT;
	
	protected Stroked(String id) {super(id);}

	/**Sets any fill-related properties.*/
	public void set(String name, Object value) {
		if (contains(StrokeProperty.class, name)) {
			Strokes.ColoredStroke cs= Strokes.modify(name, value, outlineStyle, outlinePaint);
			outlineStyle = cs.style;
			outlinePaint = cs.paint;
		} else {super.set(name, value);}
	}
	
	/**Gets fill-related properties.*/
	public Object get(String name) {
		if (contains(StrokeProperty.class, name)) {
			return Strokes.get(name, outlineStyle, outlinePaint);
		} else {return super.get(name);}
	}

	protected void render(Graphics2D g, Shape s) {
		if (outlinePaint != null && outlineStyle != null) {
			g.setStroke(outlineStyle);
			g.setPaint(outlinePaint);
			g.draw(s);
		}
	}
	
}
