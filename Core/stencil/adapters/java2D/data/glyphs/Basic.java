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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.regex.Pattern;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;
import static stencil.adapters.general.Registrations.*;

public abstract class Basic implements Glyph2D {
	public static Color DEBUG_COLOR = null;
	private static final Stroke DEBUG_STROKE = new BasicStroke(.25f);
	protected static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
	
	protected static final Attribute ID = new Attribute(StandardAttribute.ID);
	protected static final Attribute LAYERNAME = new Attribute(StandardAttribute.LAYERNAME);
	protected static final Attribute IMPLANTATION = new Attribute(StandardAttribute.IMPLANTATION);

	protected static final Attribute<Registrations.Registration> REGISTRATION = new Attribute(StandardAttribute.REGISTRATION);

	protected static final Attribute<Boolean> VISIBLE = new Attribute("VISIBLE", true);
	
	protected static final AttributeList ATTRIBUTES = new AttributeList();
	static {
		ATTRIBUTES.add(ID);

		ATTRIBUTES.add(LAYERNAME);
		ATTRIBUTES.add(IMPLANTATION);
		ATTRIBUTES.add(VISIBLE);
		ATTRIBUTES.add(REGISTRATION);
	}
	
	protected final String id;
	
	protected final Registration registration;
	
	/**What layer does this glyph belong to?*/
	protected final DisplayLayer layer;
	
	/**Should this glyph be drawn?**/
	protected final boolean visible;
	
	protected final Rectangle2D bounds = new Rectangle2D.Double();
	
	protected Basic(DisplayLayer layer, String id) {
		this.layer = layer;

		this.id = id;
		visible = VISIBLE.defaultValue;
		registration = REGISTRATION.defaultValue;
	}

	protected Basic(String id, Basic source) {
		this.id = id;
		this.layer = source.layer;
		this.registration = source.registration;
		this.visible = source.visible;
		updateBoundsRef(source.bounds);
	}
	
	protected Basic(Basic source, Tuple option, AttributeList unsettables) {
		validateOptions(option, unsettables);
		this.layer = source.layer;
		updateBoundsRef(source.getBoundsReference());
		id = switchCopy(source.id, (String) safeGet(option, ID));
		visible = switchCopy(source.visible, safeGet(option, VISIBLE));
		registration = switchCopy(source.registration, safeGet(option, REGISTRATION));		
	}
	
	/**What is the name of this implantation?*/
	public abstract String getImplantation();

	/**Return a list of all of the attributes of this glyph.*/
	protected abstract AttributeList getAttributes();
	
	/**Which of the attributes cannot be set?
	 * Unsettable attributes are typically those derived from other attributes
	 * (e.g. width/height on a shape are derived from the settable SIZE attribute).
	 * */
	protected abstract AttributeList getUnsettables();
	
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
	public final Rectangle2D getBoundsReference() {return bounds;}
	
	/**Internal method for setting the bounds.  
	 * Bounds are final, but it is often cumbersome to 
	 * set them in the super constructor chain.  This method
	 * allows an internal update to occur.  Good practice indicates
	 * that this method should be called exactly once for each instance
	 * of a glyph before the constructor completes.  
	 */
	protected final void updateBoundsRef(Rectangle2D r) {bounds.setRect(r);}

	public int size() {return getAttributes().size();}
	public Object get(int idx) {
		Attribute a = getAttributes().get(idx);
		return get(a.name);
	}
	
	public Object get(String name) {
		if (ID.is(name)) {return id;}
		if (LAYERNAME.is(name)) {return layer==null?null:layer.getName();}
		if (IMPLANTATION.is(name)) {return getImplantation();}
		if (REGISTRATION.is(name)) {return registration;}
		if (VISIBLE.is(name)) {return visible;}
		throw new InvalidNameException(name, getPrototype());
	}


	public String getLayerName() {return layer==null?null:layer.getName();}
	
	public List<String> getPrototype() {return getAttributes().getNames();}

	public boolean hasField(String name) {return getAttributes().getNames().contains(name);}

	public boolean isDefault(String name, Object value) {
		Object def = getAttributes().getDefault(name);
		return ((def == value) || (def != null) && def.equals(value));
	}
	
	/**Outputs a string representation of this tuple.
	 * Only reports settable fields.
	 **/
	public String toString() {
		List<String> includeFields = getPrototype();
		List<String> omitFields = getUnsettables().getNames();
		
		includeFields.removeAll(omitFields);
		
		return Tuples.toString(this, includeFields);
	}
	
	public DisplayLayer getLayer() {return layer;}
	
	public String getID() {return id;}
	
	public boolean isVisible() {return visible;}
	
	/**Get a value from the passed tuple; return null if the field is not present in the tuple.*/
	protected static final <T> T safeGet(Tuple source, Attribute<T> att) {
		String field = att.name;
		return (!source.getPrototype().contains(field)) ? null : (T) Converter.convert(source.get(field), att.type);
	}
	
	/**REturn either the candidate value -or- if it is null, the defaultValue.*/
	protected static final <T> T switchCopy(T defaultValue, T candidate) {
		return candidate==null ? defaultValue : candidate;
	}
	
	/**Ensure that the tuple passed does not have any of the listed 'unsettable' fields.*/
	protected final void validateOptions(Tuple t, AttributeList unsettables) {
		for (Attribute att: unsettables) {
			if (t.getPrototype().contains(att.name)) {
				throw new IllegalArgumentException(String.format("Cannot set field %1$s on glyph of type %2$s", att.name, getImplantation()));
			}
		}
	}
	
	/**Post rendering actions are to restore the transform and draw debugging entities (if requested).
	 * The transform passed should be the same as the one returned from preRender.
	 * 
	 */
	protected void postRender(Graphics2D g, AffineTransform restore) {
		if (restore != null) {g.setTransform(restore);}			
		if (DEBUG_COLOR != null) {
			
			//Bounds for clip calculation
			Rectangle2D r =getBoundsReference();
			g.setPaint(DEBUG_COLOR);
			g.setStroke(DEBUG_STROKE);
			g.draw(r);
			
//			//Bounds as reported
//			try {
//				double x = (Double) this.get("X", Double.class);
//				double y = (Double) this.get("Y", Double.class);
//				double w = (Double) this.get("WIDTH", Double.class);
//				double h = (Double) this.get("HEIGHT", Double.class);
//				r = new Rectangle2D.Double(x,y,w,h);
//				g.draw(r);
//			} catch  (Exception e) {/*Exception ignored, its just debug code.*/}

			//Registration point
			try {
				g.setPaint(DEBUG_COLOR.darker());
				double scale=2;
				double x = Converter.toDouble(this.get("X"));
				double y =  Converter.toDouble(this.get("Y"));
				g.fill(Shapes.getShape(StandardShape.CROSS, x-scale/2, y-scale/2, scale, scale));
			} catch  (Exception e) {/*Exception ignored, its just debug code.*/}

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
	public static String nameArgs(String fullName) {
		String baseName = baseName(fullName);
		if (baseName.equals(fullName)) {return null;}

		return fullName.substring(baseName(fullName).length());
	}
	
	/**Given two registration and point descriptions (potentially), what should the top-left
	 * of a new glyph be if it were the merge of source and option?
	 *  
	 * @param targetWidth The width of the newly formed thing
	 * @param targetHeight The height of the newly formed thing
	 */
	public static Point2D mergeRegistrations(Basic source, Tuple option, double targetWidth, double targetHeight, Attribute<Double> X, Attribute<Double> Y) {
		double x = switchCopy(Converter.toDouble(source.get(X.name)), safeGet(option, X));
		double y = switchCopy(Converter.toDouble(source.get(Y.name)), safeGet(option, Y));			
		Registrations.Registration reg = switchCopy(source.registration, safeGet(option, REGISTRATION));
		
		//If registrations are split between before and after AND the coordinates are also split, take the old partial value to the new registration system
		if (source.registration != reg) {
			if (option.getPrototype().contains(X.name) && !option.getPrototype().contains(Y.name)) {	
				y = Registrations.topLeftToRegistration(reg, source.getBoundsReference()).getY();	
			} else if (!option.getPrototype().contains(X.name) && option.getPrototype().contains(Y.name)) {
				x = Registrations.topLeftToRegistration(reg, source.getBoundsReference()).getX();
			}
			//Any other case and both X and Y came from the same source, so it will be registration consistent.
		}		

		//Knowing the registration coordiantes, what is the new top left?
		Point2D topLeft = Registrations.registrationToTopLeft(reg, x,y, targetHeight, targetWidth);
		return topLeft;
	}
}
