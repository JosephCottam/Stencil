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
package stencil.adapters.piccoloDynamic.glyphs;


import static stencil.adapters.general.Fills.FillProperty;
import static stencil.adapters.general.Strokes.StrokeProperty;
import static stencil.types.color.NamedColor.CLEAR;

import java.awt.geom.GeneralPath;

import stencil.adapters.general.Shapes;
import stencil.adapters.general.Fills;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;


public class Shape extends Path {
	public static final String IMPLANTATION_NAME  = "SHAPE";

	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : Path.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute("SHAPE", "getShape", "setShape", Shape.class, StandardShape.ELLIPSE));
		PROVIDED_ATTRIBUTES.put(new Attribute("SIZE",  "getSize", "setSize", Shape.class, new Double(1)));

		//Make the outline default to clear
		PROVIDED_ATTRIBUTES.put(PROVIDED_ATTRIBUTES.get(StrokeProperty.STROKE_COLOR).changeDefault(CLEAR));
		
		
		
		for (FillProperty prop: FillProperty.values()) {
			Object def = prop.getDefaultValue();
			Class clss = prop.getType();
			if (clss == Float.class) {clss = Double.class;}
			PROVIDED_ATTRIBUTES.put(new Attribute(prop.name(), "getFillPart", "setFillPart", Shape.class, true, def, clss));
		}

	}

	protected StandardShape shapeName = (StandardShape) PROVIDED_ATTRIBUTES.get("SHAPE").defaultValue;

	public Shape(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);
		setFill(Fills.getDefault());
		applyDefaults();
	}

	public StandardShape getShape() {return shapeName;}
	public void setShape(StandardShape name) {
		this.shapeName = name;
		signalBoundsChanged();
	}

	public Double getSize() {return Math.max(this.getWidth(), this.getHeight());}
	public void setSize(Double size) {
		assert (size >=0) : "Size cannot be negative";

		setWidth(size);
		setHeight(size);
	}

	public Object getFillPart(String name) {return Fills.get(name, getFill());}
	public void setFillPart(String name, Object value) {setFill(Fills.modify(getFill(), name, value));}

	public java.awt.Paint getFill() {return path.getPaint();}
	public void setFill(java.awt.Paint paint) {
		path.setPaint(paint);
	}

	public void signalBoundsChanged() {
		verifyShape();
		super.signalBoundsChanged();
	}

	private void verifyShape() {
		java.awt.Shape s = Shapes.getShape(shapeName, this.getBounds().getBounds2D());
		if (s == null) {super.setPath(new GeneralPath());}
		else {super.setPath(s);}
	}

}
