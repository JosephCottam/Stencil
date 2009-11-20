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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public class Shape extends Filled {
	private static final AttributeList ATTRIBUTES = new AttributeList(Filled.ATTRIBUTES);
	private static final AttributeList UNSETTABLES = new AttributeList();
	private static final String IMPLANTATION = "SHAPE";
	
	private static final Attribute<StandardShape> SHAPE = new Attribute("SHAPE", StandardShape.ELLIPSE);
	private static final Attribute<Double> ROTATION = new Attribute("ROTATION", 0d);
	private static final Attribute<Double> SIZE = new Attribute("SIZE", 1.0d);
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	
	static {
		ATTRIBUTES.add(SHAPE);
		ATTRIBUTES.add(SIZE);
		ATTRIBUTES.add(ROTATION);
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
	}
	
	private final SoftReference<java.awt.Shape> glyphRef;

	private final StandardShape shape;
	private final double size;
	
	private final double rotation;
	private final double regX;
	private final double regY;
	
	public Shape(DisplayLayer layer, String id) {
		super(layer, id);
		
		shape =  SHAPE.defaultValue;
		size = SIZE.defaultValue;
		regX = X.defaultValue;
		regY = Y.defaultValue;
		rotation = ROTATION.defaultValue;	
		
		java.awt.Shape glyph = createShape();
		glyphRef  = new SoftReference(glyph);
		super.updateBoundsRef(glyph.getBounds2D());
	}
	
	
	
	private Shape(String id, Shape source) {
		super(id, source);
	
		this.glyphRef = source.glyphRef;
		this.shape = source.shape;
		this.size = source.size;
		this.rotation = source.rotation;
		this.regX = source.regX;
		this.regY = source.regY;
	}



	protected Shape(Shape source, Tuple option) {
		super(source, option, UNSETTABLES);
		
		shape = switchCopy(source.shape, safeGet(option, SHAPE));
		size = switchCopy(source.size, safeGet(option, SIZE));
		rotation = switchCopy(source.rotation, safeGet(option, ROTATION));
		
		Point2D topLeft = mergeRegistrations(source, option, size, size, X, Y);
		Point2D reg = Registrations.topLeftToRegistration(registration, topLeft.getX(), topLeft.getY(), size, size);
		
		regX = reg.getX();
		regY = reg.getY();

		java.awt.Shape glyph = createShape();
		glyphRef = new SoftReference(glyph);
		super.updateBoundsRef(glyph.getBounds2D());
	}

	private final java.awt.Shape createShape() {
		Point2D topLeft = Registrations.registrationToTopLeft(registration, regX, regY, size, size);
		Rectangle2D bounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), size, size);
		
		java.awt.Shape s = Shapes.getShape(shape, bounds.getX()-regX, bounds.getY()-regY, size, size);
		s = AffineTransform.getRotateInstance(Math.toRadians(rotation)).createTransformedShape(s);
		s = AffineTransform.getTranslateInstance(regX, regY).createTransformedShape(s);
		
		return s;
	}
	
	public Object get(String name) {
			 if (SHAPE.is(name)) {return shape;}
		else if (X.is(name)) {return regX;}
		else if (Y.is(name)) {return regY;}
		else if (SIZE.is(name)) {return size;}
		else if (ROTATION.is(name)) {return rotation;}
		else {return super.get(name);}
	}
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}
	
	public String getImplantation() {return IMPLANTATION;}

	@Override
	public void render(Graphics2D g, AffineTransform base) {
		if (bounds.getWidth() ==0 || bounds.getHeight() ==0) {return;}
		java.awt.Shape glyph = glyphRef.get();
		if (glyph == null) {glyph = createShape();}
		
		super.render(g, glyph);
		super.postRender(g, null);
	}

	public Shape update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}
		return new Shape(this, t);
	}
	
	public Shape updateID(String id) {return new Shape(id, this);}	

}
