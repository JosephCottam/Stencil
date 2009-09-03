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

import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.geom.Point2D;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.TextFormats;
import stencil.adapters.general.TextFormats.TextProperty;
import stencil.adapters.piccoloDynamic.util.*;


public class Text extends CommonNode {
	public static final String IMPLANTATION_NAME = "TEXT";

	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : CommonNode.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute("TEXT", "getText", "setText", Text.class, null, String.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.WIDTH.name(), "getWidth", "setWidth", Text.class, null, Double.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.HEIGHT.name(), "getHeight", "setHeight", Text.class, null, Double.class));
		
		for (TextProperty prop: TextProperty.values()) {
			Object def = prop.getDefaultValue();
			Class clss = prop.getType();
			PROVIDED_ATTRIBUTES.put(new Attribute(prop.name(), "getFormatPart", "setFormatPart", Text.class, true, def, clss));
		}
	}

	protected PText text = new PText();

	public Text(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);
		super.setChild(text);
		applyDefaults();
	}

	public String getText() {return text.getText();}
	public void setText(String newText) {
		Point2D regPoint = Registrations.topLeftToRegistration(registration, getXPosition(), getYPosition(), getWidth(), getHeight());
		text.setText(newText);
		setPosition(regPoint);
		this.setChildBoundsInvalid(true);
	}

	public void setFormatPart(String att, Object value) {
		TextFormats.Format f = TextFormats.set(att, value, text.getFont(), text.getTextPaint(), text.getJustification());
		
		text.setFont(f.font);
		text.setTextPaint(f.textColor);
		text.setJustification(f.justification);
		
		this.setChildBoundsInvalid(true);
	}

	public Object getFormatPart(String att) {
		return TextFormats.get(att, text.getFont(), text.getTextPaint(), text.getJustification());
	}


	public boolean setWidth(double width) {
		boolean rv = (width == super.getWidth());
		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());

		if (width == 0) {
			text.setConstrainWidthToTextWidth(true);
		} else {
			text.setConstrainWidthToTextWidth(false);
			text.setWidth(width);
		}
		text.recomputeLayout();
		setPosition(regPoint);
		return rv;
	}

	public PBounds getBoundsReference() {return text.getBoundsReference();}
	
	public boolean setHeight(double height) {
		boolean rv = (height == super.getWidth());

		Point2D regPoint = Registrations.topLeftToRegistration(registration, this.getXTranslate(), this.getYTranslate(), this.getWidth(), this.getHeight());

		if (height == 0) {
			text.setConstrainHeightToTextHeight(true);
		} else {
			text.setConstrainHeightToTextHeight(false);
			text.setHeight(height);
		}
		text.recomputeLayout();
		setPosition(regPoint);
		return rv;
	}	
}
