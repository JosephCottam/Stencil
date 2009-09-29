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
package stencil.adapters.java2D.data.immutibles;

import static stencil.util.enums.EnumUtils.contains;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import stencil.adapters.general.Fills;
import stencil.adapters.general.Strokes;
import stencil.adapters.general.Fills.FillProperty;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.Tuple;
import stencil.types.color.Color;

public abstract class Filled extends Stroked {
	protected static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);;
	static {
		for (FillProperty p: FillProperty.values()) {ATTRIBUTES.add(new Attribute(p));}
	}
	
	protected final Paint fill;
	
	protected Filled(Table layer, String id) {
		super(layer, id, Strokes.DEFAULT_STROKE, new java.awt.Color(0,0,0, Color.CLEAR_INT));
		fill = Fills.getDefault();
	}
	
	protected Filled(Table t, Stroked source, Tuple option, AttributeList unsettables) {
		super(t, source, option, unsettables);
		fill = Fills.make(source, option);
	}

	public Object get(String name) {
		if (contains(FillProperty.class, name)) {
			return Fills.get(name, fill);
		} else {return super.get(name);}
	}
	
	protected void render(Graphics2D g, Shape s) {
		assert s !=  null : "Cannot render null shape";
		
		if (!Color.isTransparent(fill)) {
			g.setPaint(fill);
			g.fill(s);
		}
		super.render(g, s);
	}
}
