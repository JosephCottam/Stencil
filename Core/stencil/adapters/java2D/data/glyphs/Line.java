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


import java.awt.geom.Line2D;
import java.awt.Graphics2D;

import stencil.types.Converter;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import static stencil.adapters.GlyphAttributes.StandardAttribute.*;

public final class Line extends Stroked {
	protected static final AttributeList attributes;
	static {
		attributes = new AttributeList(Stroked.attributes);

		attributes.add(new Attribute("Xn", Xn.getDefaultValue(), Xn.getType()));
		attributes.add(new Attribute("Yn", Yn.getDefaultValue(), Yn.getType()));
		
		attributes.remove(StandardAttribute.HEIGHT);
		attributes.remove(StandardAttribute.WIDTH);
	}

	private double x1 = (Double) attributes.get(X.name()).defaultValue;
	private double y1 = (Double) attributes.get(X.name()).defaultValue;
	private double x2 = (Double) attributes.get(X.name()).defaultValue;
	private double y2 = (Double) attributes.get(X.name()).defaultValue;
	
	public Line(String id) {super(id);}

	public Double getHeight() {return Math.abs(y1-y2);}

	public Double getWidth() {return Math.abs(x1-x2);}
	
	public String getImplantation() {return "LINE";} 

	protected AttributeList getAttributes() {return attributes;}
	
	public Object get(String name) {
		if (name.equals("X.1")) 	 {return x1;}
		else if (name.equals("Y.1")) {return y1;}
		else if (name.equals("X.2")) {return x2;}
		else if (name.equals("Y.2")) {return y2;}
		else if (name.equals("Xn"))  {return new Double[]{x1,x2};}
		else if (name.equals("Yn"))  {return new Double[]{y1,y2};}
		else{return super.get(name);}		
	}
	
	public void set(String name, Object value) {
		if (name.equals("X.1")) 	 {x1 = Converter.toDouble(value);}
		else if (name.equals("Y.1")) {y1 = Converter.toDouble(value);}
		else if (name.equals("X.2")) {x2 = Converter.toDouble(value);}
		else if (name.equals("Y.2")) {y2 = Converter.toDouble(value);}
		else{super.set(name, value);}
	}
	
	public void render(Graphics2D g) {
		Line2D l = new Line2D.Double(x1,y1,x2,y2);
		super.render(g,l);		
	}
}
