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

import stencil.adapters.GlyphAttributes.StandardAttribute;

import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.types.Converter;

public final class Image extends Point {
	private static final double AUTO_SCALE = -1;
	
	private static final Attribute HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SCALE);
	private static final Attribute WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SCALE);
	private static final Attribute FILE= new Attribute("FILE", null, String.class);
	protected static final AttributeList attributes = new AttributeList();

	static {
		attributes.add(FILE);
		attributes.add(HEIGHT);
		attributes.add(WIDTH);
	}
	
	private java.awt.image.BufferedImage i;
	private String filename = (String) FILE.defaultValue;

	private double width = (Double) HEIGHT.defaultValue;
	private double height = (Double) WIDTH.defaultValue;
	
	public Image(String id) {super(id);}

	protected AttributeList getAttributes() {return attributes;}

	public void set(String name, Object value) {
			 if (HEIGHT.is(name)) 	{height= Converter.toDouble(value); verifyImage();}
		else if (WIDTH.is(name)) 	{width = Converter.toDouble(value); verifyImage();}
		else if (FILE.is(name)) {filename = Converter.toString(value); verifyImage();}
		else						{super.set(name, value);}
	}
	
	public Object get(String name) {
		if (HEIGHT.is(name)) 	{return height;}
		if (WIDTH.is(name)) 	{return width;}
		if (FILE.is(name)) 	{return filename;}
		
		return super.get(name);
	}
	
	public String getImplantation() {return "IMAGE";}

	public double getHeight() {
		if (i ==null) {return 0;}
		else if (height != AUTO_SCALE) {return height;}
		return i.getHeight();
	}

	public double getWidth() {
		if (i ==null) {return 0;}
		else if (width != AUTO_SCALE) {return width;}
		return i.getWidth();
	}
	
	/**Given the current information, make sure the image is 
	 * ready to be rendered.
	 */
	private void verifyImage() {
		//TODO: Implement
	}

	@Override
	public void render(Graphics2D g) {
		AffineTransform rs = super.preRender(g);
		super.postRender(g,rs);
		//TODO: Actually render...
	}
}
