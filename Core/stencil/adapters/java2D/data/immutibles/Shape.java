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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.Tuple;

public class Shape extends Filled {
	protected static final class InitResult {
		GeneralPath glyph;
		Rectangle2D bounds;
		double regX;
		double regY;
	}
	
	private static final AttributeList ATTRIBUTES = new AttributeList(Stroked.ATTRIBUTES);
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
	
	private final Rectangle2D bounds;
	private final GeneralPath glyph;

	private final StandardShape shape;
	private final double size;
	
	private final double rotation;
	private final double regX;
	private final double regY;
	
	public Shape(Table layer, String id) {
		super(layer, id);
		
		shape =  SHAPE.defaultValue;
		size = SIZE.defaultValue;
		regX = X.defaultValue;
		regY = Y.defaultValue;
		rotation = ROTATION.defaultValue;	
		
		InitResult rs = initWork();
		bounds = rs.bounds;
		glyph = rs.glyph;
	}
	
	protected Shape(Table t, Shape source, Tuple option) {
		super(t, source, option, UNSETTABLES);
		
		shape = switchCopy(source.shape, safeGet(option, SHAPE));
		size = switchCopy(source.size, safeGet(option, SIZE));
		rotation = switchCopy(source.rotation, safeGet(option, ROTATION));
		
		Point2D topLeft = mergeRegistrations(source, option, size, size, X, Y);
		Point2D reg = Registrations.topLeftToRegistration(registration, topLeft.getX(), topLeft.getY(), size, size);
		
		regX = reg.getX();
		regY = reg.getY();
		
		InitResult rs = initWork();
		bounds = rs.bounds;
		glyph = rs.glyph;
	}

	private InitResult initWork() {
		Point2D topLeft = Registrations.registrationToTopLeft(registration, regX, regY, size, size);
		Rectangle2D bounds = new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), size, size);
		
		java.awt.Shape s = Shapes.getShape(shape, bounds.getX()-regX, bounds.getY()-regY, size, size);
		GeneralPath p = new GeneralPath(s);
		
		p.transform(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		p.transform(AffineTransform.getTranslateInstance(regX, regY));
		
		InitResult rv = new InitResult();
		rv.bounds = p.getBounds2D();
		rv.glyph = p;
		
		return rv;
	}
	
	public Object get(String name) {
			 if (SHAPE.is(name)) {return shape;}
		else if (X.is(name)) {return regX;}
		else if (Y.is(name)) {return regY;}
		else if (SIZE.is(name)) {return size;}
		else {return super.get(name);}
	}
	
	protected AttributeList getAttributes() {return ATTRIBUTES;}
	public Rectangle2D getBoundsReference() {return bounds;}
	public String getImplantation() {return IMPLANTATION;}

	@Override
	public void render(Graphics2D g, AffineTransform base) {
		if (bounds.getWidth() ==0 || bounds.getHeight() ==0) {return;}
	
		super.render(g, glyph);
		super.postRender(g, null);
	}

	public Shape update(Tuple t) throws IllegalArgumentException {return new Shape(this.layer, this, t);}
	public Shape updateLayer(Table t) {return new Shape(t, this, Tuple.EMPTY_TUPLE);}

}
