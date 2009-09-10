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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.general.Registrations;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.types.Converter;
import stencil.util.Tuples;
import static stencil.adapters.general.Registrations.*;
import static stencil.adapters.GlyphAttributes.StandardAttribute.*;

public abstract class Point implements Glyph2D {
	protected static final AttributeList attributes = new AttributeList();
	static {
		attributes.add(new Attribute(ID));
		attributes.add(new Attribute(X));
		attributes.add(new Attribute(Y));
		attributes.add(new Attribute(Z));

		attributes.add(new Attribute(WIDTH));
		attributes.add(new Attribute(HEIGHT));
		attributes.add(new Attribute(LAYERNAME));
		attributes.add(new Attribute(IMPLANTATION));
		attributes.add(new Attribute(REGISTRATION));
		attributes.add(new Attribute(ROTATION));
	}
	
	protected Set<Integer> markers = new TreeSet<Integer>();
	
	protected String id = (String) attributes.get(ID).defaultValue;
	
	/**The value stored here must be interpreted with respect to registration
	 * before it can be used for rendering. Use the 'correctRegistrtion' method.
	 */
	protected Double x = (Double) attributes.get(X).defaultValue;

	/**The value stored here must be interpreted with respect to registration
	 * before it can be used for rendering.  Use the 'correctRegistrtion' method.
	 */
	protected double y = (Double) attributes.get(Y).defaultValue;
	protected double z = (Double) attributes.get(Z).defaultValue;
	protected Registration registration = (Registration) attributes.get(REGISTRATION).defaultValue;
	protected double rotation = (Double) attributes.get(ROTATION).defaultValue;
	
	protected Table layer;
	
	protected Point(String id) {this.id = id;}
	
	/**How wide is this glyph?*/
	public abstract double getWidth();
	
	/**How tall is this glyph?*/
	public abstract double getHeight();
	
	/**What is the name of this implantation?*/
	public abstract String getImplantation();

	/**Return a list of all of the attributes of this glyph.*/
	protected abstract AttributeList getAttributes();
	
	/**Render the glyph to the given graphics object.*/
	public abstract void render(Graphics2D g);

	/**What are the actual bounds of this glyph?  
	 * The X and Y of this may not match those stored in 'x' and 'y' because
	 * the actual bounds will be as rendered on the screen, and 
	 * therefore will always be the top-left corner.
	 */
	public Rectangle2D getBounds() {
		double x,y,w,h;
		w = getWidth();
		h= getHeight();
		x = this.x;
		y = this.y;
		Point2D r = Registrations.registrationToTopLeft(registration, x,y, w,h);		
		return new Rectangle2D.Double(r.getX(), r.getY(),w,h);
	}
	
	
	/**Duplicate the current glyph, but give it a new ID.
	 * 
	 * Assumes there is a constructor which takes the ID as its only argument. 
	 * 
	 * 
	 * TODO: REMOVE WHEN THE IMMUTIBLE TUPLE CONVERSION IS COMPLETE (duplicate is replaced by 'update' then)
	 * */
	public Glyph2D duplicate(String ID) {
		try {
			Constructor c = this.getClass().getConstructor(String.class);
			Point g = (Point) c.newInstance(ID);
			g.updateNonID(this);
			return g;
		} catch (Exception e) {throw new Error("Error duplicating glyph:" + this.toString(), e);}
	}	

	public void set(String name, Object value) {
		if (name.equals(X.name())) {this.x = Converter.toDouble(value);}
		else if (name.equals(Y.name())) {this.y = Converter.toDouble(value);}
		else if (name.equals(Z.name())) {this.z = Converter.toDouble(value);}
		else if (name.equals(ROTATION.name())) {this.rotation = Converter.toDouble(value);}
		else if (name.equals(REGISTRATION.name())) {this.registration = (Registration) Converter.convert(value, Registration.class);}
		else if (name.equals(ID.name())) {this.id = Converter.toString(value);}
		else if (name.equals(IMPLANTATION)) {throw new IllegalArgumentException("Cannot set implantation.");}
		else {throw new InvalidNameException(name, getFields());}
	}
	
	public Object get(String name) {
		if (name.equals(ID.name())) {return id;}
		if (name.equals(X.name())) {return x;}
		if (name.equals(Y.name())) {return y;}
		if (name.equals(Z.name())) {return z;}

		if (name.equals(WIDTH.name())) {return getWidth();}
		if (name.equals(HEIGHT.name())) {return getHeight();}
		if (name.equals(LAYERNAME.name())) {return layer==null?null:layer.getName();}
		if (name.equals(IMPLANTATION.name())) {return getImplantation();}
		if (name.equals(REGISTRATION.name())) {return registration;}
		if (name.equals(ROTATION.name())) {return rotation;}
		throw new InvalidNameException(name, getFields());
	}

	public String getLayerName() {
		return layer==null?null:layer.getName();
	}
	
	public Object get(String name, Class<?> type) throws IllegalArgumentException, InvalidNameException {
		return Converter.convert(get(name), type);
	}
	
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
		
	public String toString() {return Tuples.toString(this);}
	
	public void setLayer(Table layer) {this.layer =layer;}
	public Table getLayer() {return layer;}
	
	public String getID() {return id;}
	
	public Glyph2D update(String field, Object value) {
		Object existing = this.get(field);
		if (existing == value || (existing !=null && existing.equals(value))) {
			return this;
		} else if (field.equals("ID") && !value.equals(this.getID())){
			return duplicate((String) value);
		}  else {
			set(field, value); 
			return this;
		}
	}

	
	private Glyph2D updateNonID(Tuple source) {return update(source, false);}
	public Glyph2D update(Tuple source) {return update(source, true);}
	private Glyph2D update(Tuple source, boolean id) {
		Glyph2D temp = this;
		for (String field: source.getFields()) {
			if (!id && field.equals("ID")) {continue;}
			temp = temp.update(field, source.get(field));
		}
		return temp;
	}
	
	public boolean hasMarker(int marker) {return  markers.contains(marker);}
	public void markForUpdate(int marker) {markers.add(marker);}
	
}
