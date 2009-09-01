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
import java.lang.reflect.Constructor;
import java.util.List;

import stencil.adapters.Glyph;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.InvalidNameException;
import stencil.types.Converter;
import static stencil.adapters.GlyphAttributes.StandardAttribute.*;

public abstract class Point implements Glyph {
	protected static final AttributeList attributes = new AttributeList();
	static {
		attributes.add(new Attribute(ID));
		attributes.add(new Attribute(X));
		attributes.add(new Attribute(Y));
		attributes.add(new Attribute(Z));

		attributes.add(new Attribute(WIDTH));
		attributes.add(new Attribute(HEIGHT));
	}
	
	protected Point(String id) {this.id = id;}
	
	
	public abstract Double getWidth();
	public abstract Double getHeight();
	
	/**Duplicate the current glyph, but give it a new ID.
	 * 
	 * Assumes there is a constructor which takes the ID as its only argument. 
	 * */
	public Glyph duplicate(String ID) {
		try {
			Constructor c = this.getClass().getConstructor(String.class);
			Glyph g = (Glyph) c.newInstance(ID);
			return g;
		} catch (Exception e) {throw new Error("Error duplicating tuple:" + this.toString());}
	}	
	
	protected String id = (String) attributes.get(ID.name()).getDefault();
	protected Double x = (Double) attributes.get(X.name()).getDefault();
	protected Double y = (Double) attributes.get(Y.name()).getDefault();
	protected Double z = (Double) attributes.get(Z.name()).getDefault();
	
	
	public void set(String name, Object value) {
		if (name.equals(ID.name())) {this.id = Converter.toString(value);}
		else if (name.equals(X.name())) {this.x = Converter.toDouble(value);}
		else if (name.equals(Y.name())) {this.y = Converter.toDouble(value);}
		else if (name.equals(Z.name())) {this.y = Converter.toDouble(value);}
		else {throw new InvalidNameException(name, getFields());}
	}
	
	public Object get(String name) {
		if (name.equals(ID.name())) {return id;}
		if (name.equals(X.name())) {return x;}
		if (name.equals(Y.name())) {return y;}
		if (name.equals(Z.name())) {return z;}

		if (name.equals(WIDTH.name())) {return getWidth();}
		if (name.equals(HEIGHT.name())) {return getHeight();}
		
		throw new InvalidNameException(name, getFields());
	}

	public Object get(String name, Class<?> type) throws IllegalArgumentException, InvalidNameException {
		return Converter.convert(get(name), type);
	}

	protected abstract AttributeList getAttributes();
	
	public List<String> getFields() {
		return getAttributes().getNames();
	}

	public boolean hasField(String name) {
		return getAttributes().getNames().contains(name);
	}

	public boolean isDefault(String name, Object value) {
		Object def = getAttributes().getDefault(name);
		return ((def == value) || (def != null) && def.equals(value));
	}
	
	public abstract void render(Graphics2D g);
}
