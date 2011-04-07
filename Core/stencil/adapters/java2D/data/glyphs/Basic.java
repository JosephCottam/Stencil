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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.general.Registrations;
import stencil.adapters.general.Shapes;
import stencil.adapters.general.Shapes.StandardShape;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;
import static stencil.adapters.general.Registrations.*;

public abstract class Basic implements Glyph2D {
	public static Color DEBUG_COLOR = null;
	private static final Stroke DEBUG_STROKE = new BasicStroke(.25f);
	protected static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
	
	protected static final Attribute ID = new Attribute(StandardAttribute.ID);
	public static final Attribute IMPLANTATION = new Attribute(StandardAttribute.IMPLANTATION);
	public static final Attribute<Double> Z = new Attribute(StandardAttribute.Z);

	protected static final Attribute<Registrations.Registration> REGISTRATION = new Attribute(StandardAttribute.REGISTRATION);

	protected static final Attribute<Boolean> VISIBLE = new Attribute("VISIBLE", true);
	
	protected static final AttributeList ATTRIBUTES = new AttributeList();
	static {
		ATTRIBUTES.add(ID);

		ATTRIBUTES.add(IMPLANTATION);
		ATTRIBUTES.add(VISIBLE);
		ATTRIBUTES.add(REGISTRATION);
		ATTRIBUTES.add(Z);
	}
	
	protected final String id;
	
	protected final Registration registration;
		
	/**Should this glyph be drawn?**/
	protected final boolean visible;
	
	protected final Rectangle2D bounds = new Rectangle2D.Double();
	
	protected final double z;
	
	protected Basic(String id) {
		this.id = id;
		visible = VISIBLE.defaultValue;
		registration = REGISTRATION.defaultValue;
		z = Z.defaultValue;
	}

	protected Basic(String id, Basic source) {
		this.id = id;
		this.registration = source.registration;
		this.visible = source.visible;
		this.z = source.z;
		updateBoundsRef(source.bounds);
	}
	
	protected Basic(Basic source, Tuple option, AttributeList unsettables) {
		validateOptions(option, unsettables);
		updateBoundsRef(source.getBoundsReference());
		id = switchCopy(source.id, (String) safeGet(option, ID));
		visible = switchCopy(source.visible, safeGet(option, VISIBLE));
		registration = switchCopy(source.registration, safeGet(option, REGISTRATION));
		z = switchCopy(source.z, safeGet(option, Z));
	}
	
	/**What is the name of this implantation?*/
	public abstract String getImplantation();

	/**Return a list of all of the attributes of this glyph.
	 * NOTE: This is almost always the same as the prototype...
	 * */
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
		if (IMPLANTATION.is(name)) {return getImplantation();}
		if (REGISTRATION.is(name)) {return registration;}
		if (VISIBLE.is(name)) {return visible;}
		if (Z.is(name)) {return z;}
		throw new InvalidNameException(name, getPrototype());
	}


	public boolean hasField(String name) {return getAttributes().getNames().contains(name);}

	public boolean isDefault(String name, Object value) {
		Object def = getAttributes().getDefault(name);
		return ((def == value) || (def != null) && def.equals(value));
	}
	
	/**Outputs a string representation of this tuple.
	 * Only reports settable fields.
	 **/
	public String toString() {
		List<String> includeFields = new ArrayList(Arrays.asList(TuplePrototypes.getNames(getPrototype())));
		List<String> omitFields = getUnsettables().getNames();
		
		includeFields.removeAll(omitFields);
		
		String[] fields = includeFields.toArray(new String[includeFields.size()]);
		return Tuples.toString(this, fields);
	}
	
	public String getID() {return id;}
	public double getZ() {return z;}
	public boolean isVisible() {return visible;}
	
	/**Get a value from the passed tuple; return null if the field is not present in the tuple.*/
	protected static final <T> T safeGet(Tuple source, Attribute<T> att) {
		return Tuples.safeGet(att.name, source, source.getPrototype(), att.type, null);
	}
	
	/**Return either the candidate value -or- if it is null, the defaultValue.*/
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
}
