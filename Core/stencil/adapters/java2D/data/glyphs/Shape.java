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


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import stencil.types.Converter;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.util.AttributeList;
import stencil.adapters.java2D.util.Attribute;
 
public final class Shape extends Filled {
	
	private static final Attribute SHAPE = new Attribute("SHAPE", StandardShape.ELLIPSE);
	private static final Attribute SIZE = new Attribute("SIZE", 1.0d);
	private static final AttributeList attributes;
	
	static {
		attributes = new AttributeList(Filled.attributes);
		attributes.add(SHAPE);
		attributes.add(SIZE);
		
		attributes.remove(StandardAttribute.WIDTH);
		attributes.remove(StandardAttribute.HEIGHT);
	}
	
	public Shape(String id) {super(id);}
	
	private StandardShape shape;
	private Double size;
	
	public void set(String name, Object value) {
		if (SHAPE.is(name)) {this.shape = (StandardShape) Converter.convert(value, StandardShape.class);}
		else if (SIZE.is(name)) {this.size = Converter.toDouble(value);}
		else {super.set(name,value);}
	}
	
	public Object get(String name) {
		if (SHAPE.is(name)) {return getShape();}
		if (SIZE.is(name)) {return getSize();}
		return super.get(name);
	}
	
	public StandardShape getShape() {return shape;}
	public Double getSize() {return size;}

	public Double getWidth() {return size;}
	public Double getHeight() {return size;}
	public String getImplantation() {return "SHAPE";}
	
	public void render(Graphics2D g) {
		if (shape == StandardShape.NONE) {return;}

		Point2D renderPoint = correctRegistration();
		java.awt.Shape s = Shapes.getShape(shape, new Rectangle2D.Double(renderPoint.getX(),renderPoint.getY(), size,size));
		
		super.render(g, s);
	}

	protected AttributeList getAttributes() {return attributes;}
}
