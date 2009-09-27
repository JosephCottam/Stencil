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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.regex.Pattern;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.types.Converter;
import stencil.util.Tuples;
import static stencil.adapters.general.Registrations.*;

public abstract class Point implements Glyph2D {
	public static Color DEBUG_COLOR = null;
	private static final Stroke DEBUG_STROKE = new BasicStroke(.25f);
	
	protected static final Attribute ID = new Attribute(StandardAttribute.ID);
	protected static final Attribute X = new Attribute(StandardAttribute.X);
	protected static final Attribute Y = new Attribute(StandardAttribute.Y);
	protected static final Attribute Z = new Attribute(StandardAttribute.Z);
	protected static final Attribute WIDTH = new Attribute(StandardAttribute.WIDTH);
	protected static final Attribute HEIGHT = new Attribute(StandardAttribute.HEIGHT);
	protected static final Attribute LAYERNAME = new Attribute(StandardAttribute.LAYERNAME);
	protected static final Attribute IMPLANTATION = new Attribute(StandardAttribute.IMPLANTATION);
	protected static final Attribute REGISTRATION = new Attribute(StandardAttribute.REGISTRATION);
	protected static final Attribute ROTATION = new Attribute(StandardAttribute.ROTATION);
	protected static final Attribute VISIBLE = new Attribute("VISIBLE", true);

	
	
	protected static final AttributeList attributes = new AttributeList();
	static {
		attributes.add(ID);
		attributes.add(X);
		attributes.add(Y);
		attributes.add(Z);

		attributes.add(WIDTH);
		attributes.add(HEIGHT);
		attributes.add(LAYERNAME);
		attributes.add(IMPLANTATION);
		attributes.add(REGISTRATION);
		attributes.add(ROTATION);
		attributes.add(VISIBLE);
	}
	
	protected final String id;
	
	/**The value stored here must be interpreted with respect to registration
	 * before it can be used for rendering. 
	 */
	protected Double x = (Double) attributes.get(StandardAttribute.X).defaultValue;

	/**The value stored here must be interpreted with respect to registration
	 * before it can be used for rendering. 
	 */
	protected double y = (Double) attributes.get(StandardAttribute.Y).defaultValue;
	protected double z = (Double) attributes.get(StandardAttribute.Z).defaultValue;
	protected Registration registration = (Registration) attributes.get(StandardAttribute.REGISTRATION).defaultValue;
	
	/**The rotation, stored in degrees.*/
	protected double rotation = (Double) attributes.get(StandardAttribute.ROTATION).defaultValue;
	
	/**What layer does this glyph belong to?*/
	protected Table layer;
	
	/**Should this glyph be drawn?**/
	protected boolean visible = (Boolean) attributes.get("VISIBLE").defaultValue;
	
	protected Point(String id) {this.id = id;}
	protected Point(Point source, Tuple option, AttributeList unsettables) {
		validateOptions(option, unsettables);
		id = switchCopy(source.id, (String) safeGet(option, ID));
		
	}
	
	/**How wide is this glyph?*/
	public abstract double getWidth();
	
	/**How tall is this glyph?*/
	public abstract double getHeight();
	
	/**What is the name of this implantation?*/
	public abstract String getImplantation();

	/**Return a list of all of the attributes of this glyph.*/
	protected abstract AttributeList getAttributes();
	
	/**Render the glyph to the given graphics object.*/
	public abstract void render(Graphics2D g, AffineTransform base);

	
	/**What are the bounds of this glyph on the logical canvas?  
	 * 
	 * The X and Y of this may not match those stored in 'x' and 'y' because
	 * the bounds will be as rendered on the screen, and therefore will always 
	 * be the top-left corner.  Further changes may result from rotation
	 * (which affects X and Y even if the registration point is the top left
	 * and may modify height and width in the bounding box).
	 */
	public Rectangle2D getBounds() {
		double x,y,w,h;
		w = getWidth();
		h= getHeight();
		x = this.x;
		y = this.y;
		Point2D tl = Registrations.registrationToTopLeft(registration, x,y, w,h);
		Rectangle2D r = new Rectangle2D.Double(0,0, w, h);
		Area a = new Area(r);
		AffineTransform af = AffineTransform.getTranslateInstance(tl.getX(), tl.getY());
		af.rotate(Math.toRadians(rotation));
		a.transform(af);
		return a.getBounds2D();
	}	
	
	/**Duplicate the current glyph, may give it a new ID.
	 * 
	 * Assumes there is a constructor which takes the ID as its only argument. 
	 * */
	protected Glyph2D duplicate(String ID) {
		try {
			Constructor c = this.getClass().getConstructor(String.class);
			Point g = (Point) c.newInstance(ID);
			g.setLayer(layer);
			g.updateNonID(this);
			return g;
		} catch (Exception e) {throw new Error("Error duplicating glyph:" + this.toString(), e);}
	}	

	public void set(String name, Object value) {
			 if (X.is(name)) {this.x = Converter.toDouble(value);}
		else if (Y.is(name)) {this.y = Converter.toDouble(value);}
		else if (Z.is(name)) {this.z = Converter.toDouble(value);}
		else if (ROTATION.is(name)) {
			this.rotation = Converter.toDouble(value);
		}
		else if (REGISTRATION.is(name)) {this.registration = (Registration) Converter.convert(value, Registration.class);}
		else if (VISIBLE.is(name)) {this.visible = (Boolean) Converter.convert(value, boolean.class);}

		else if (IMPLANTATION.is(name)) {throw new IllegalArgumentException("Cannot set implantation.");}
		else if (ID.is(name)) {throw new RuntimeException("Cannot change ID.");}

		else if (LAYERNAME.is(name)) {
			if (!layer.getName().equals(value)) {throw new RuntimeException("Cannot change layer.");}

		}
		else {throw new InvalidNameException(name, getFields());}
	}
	
	public Object get(String name) {
		if (ID.is(name)) {return id;}
		if (X.is(name)) {return x;}
		if (Y.is(name)) {return y;}
		if (Z.is(name)) {return z;}

		if (WIDTH.is(name)) {return getWidth();}
		if (HEIGHT.is(name)) {return getHeight();}
		if (LAYERNAME.is(name)) {return layer==null?null:layer.getName();}
		if (IMPLANTATION.is(name)) {return getImplantation();}
		if (REGISTRATION.is(name)) {return registration;}
		if (ROTATION.is(name)) {return rotation;}
		if (VISIBLE.is(name)) {return visible;}
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
	
	public boolean isVisible() {return visible;}
	
	/**Get a value from the passed tuple; return null if the field is not present in the tuple.*/
	protected static final Object safeGet(Tuple source, Attribute att) {
		String field = att.name;
		return (source == null || !source.hasField(field)) ? null : Converter.convert(source.get(field), att.type);
	}
	
	/**REturn either the candidate value -or- if it is null, the defaultValue.*/
	protected static final <T> T switchCopy(T defaultValue, T candidate) {
		return candidate==null ? defaultValue : candidate;
	}
	
	/**Ensure that the tuple passed does not have any of the listed 'unsettable' fields.*/
	protected final void validateOptions(Tuple t, AttributeList unsettables) {
		for (Attribute att: unsettables) {
			if (t.hasField(att.name)) {
				throw new IllegalArgumentException(String.format("Cannot set field %1$s on glyph of type %2$s", att.name, getImplantation()));
			}
		}
	}
	
	public Glyph2D update(String field, Object value) {
		try {
			Object existing = this.get(field);
			if (existing == value || (existing !=null && existing.equals(value))) {
				return this;
			} else if (field.equals("ID") && !value.equals(this.getID())){
				return duplicate(Converter.toString(value)); //TODO: Should this really do a conversion here?
			}  else {
				set(field, value); 
				return this;
			}
		} catch (Exception e) {throw new RuntimeException(String.format("Error updating %1$s to %2$s", field, value),e);}
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

	
	/**Prepare the graphics object for rendering.
	 * @returns The affine transform that the graphics object originally had.  
	 * 			This should transform should be given to postRender to ensure other elements render properly.
	 *TODO:An alternative to passing around the transforms is to allocate a graphics2D object.  Is that a better idea?  
	 */
	protected void preRender(Graphics2D g) {
		if (x!=0 || y!=0) {
			Point2D p = Registrations.registrationToTopLeft(registration, x,y,getWidth(),getHeight());
			g.translate(p.getX(), p.getY());
		}
		if (rotation != 0) {g.rotate(Math.toRadians(rotation));}
	}
	
	
	/**Post rendering actions are to restore the transform and draw debugging entities (if requested).
	 * The transform passed should be the same as the one returned from preRender.
	 * 
	 */
	protected void postRender(Graphics2D g, AffineTransform restore) {
		if (restore != null) {g.setTransform(restore);}	
		if (DEBUG_COLOR != null) {
			g.setPaint(DEBUG_COLOR);
			g.setStroke(DEBUG_STROKE);
			g.draw(getBounds());
			
			g.setPaint(DEBUG_COLOR.darker());
			double scale=2;
			g.fill(Shapes.cross(x-scale/2, y-scale/2, scale));
		}
		
	}
	
	/**Convert a full property name to just a base property name.
	 * Base properties are the first component of a dotted name,
	 * but to distinguish properties that take arguments from
	 * properties that do not, base names for properties that
	 * take arguments have an 'n' appended to the end.
	 *
	 *  For example:
	 *  	FullName	BaseName	Notes
	 *  	   Y		   Y		  Simple Y that cannot take arguments
	 *  	   Y.1		   Yn		  Y that takes an argument
	 **/
	private static final Pattern NAME_SPLITTER = Pattern.compile("\\."); 
	public static String baseName(String fullName) {
		String baseName = NAME_SPLITTER.split(fullName)[0];
		if (baseName.length() == fullName.length()) {return fullName;} //Return quick if nothing changed
		baseName = baseName.concat("n");
		return baseName;
	}
	
	/**What are the implicit arguments in the full name passed?
	 * This is the converse of the baseName operation.  Anything
	 * that is not part of the base name is part of the implicit arguments.
	 *
	 */
	public String nameArgs(String fullName) {
		String baseName = baseName(fullName);
		if (baseName.equals(fullName)) {return null;}

		return fullName.substring(baseName(fullName).length());
	}
}
