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
package stencil.display;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.tuple.InvalidNameException;
import stencil.tuple.MutableTuple;
import stencil.tuple.TupleBoundsException;
import stencil.util.enums.Attribute;

/**The CanvasTuple represents an actual drawing surface
 * to the Stencil system.  This needs to be implemented by
 * an adapter for the adapter to be considered complete.
 *
 * @author jcottam
 *
 */
public interface CanvasTuple extends MutableTuple {
	public static final String CANVAS_IMPLANTATION = "STENCIL_CANVAS";

	//Other options:
	//   Imposition (POLAR, CARTESION, ELASTIC, etc)
	//   Origin Translation: 0,0 is center?  bottom left? top right?
	//   Scale directions: Does Y get positive going up or down?

	public static enum CanvasAttribute implements Attribute {
		IMPLANTATION (null, String.class),					//null is the default so it will always be reported.
		BACKGROUND_COLOR (java.awt.Color.WHITE, java.awt.Color.class),
		X (StandardAttribute.X.getDefaultValue(),StandardAttribute.X.getType()),
		Y (StandardAttribute.Y.getDefaultValue(),StandardAttribute.Y.getType()),
		WIDTH (StandardAttribute.WIDTH.getDefaultValue(),StandardAttribute.WIDTH.getType()),
		HEIGHT (StandardAttribute.HEIGHT.getDefaultValue(),StandardAttribute.HEIGHT.getType());

		final Object defaultValue;
		final Class type;

		CanvasAttribute(Object defaultValue, Class type)  {
			this.defaultValue = defaultValue;
			this.type = type;
		}

		public Class getType() {return type;}
		public Object getDefaultValue() {return defaultValue;}
	}

	
	/**Near-complete implementation of canvas tuple for a common canvas.
	 * Most canvas tuples need only extend this class and provide the mission (relatively simple) methods.
	 */
	public static abstract class SimpleCanvasTuple implements CanvasTuple {
		public static final List<String> PROTOTYPE;
		static {
			HashSet<String> s = new HashSet<String>();
			for (CanvasAttribute a: EnumSet.allOf(CanvasAttribute.class)) {s.add(a.name());}
			PROTOTYPE = Collections.unmodifiableList(new ArrayList(s));
		}
		
		protected abstract Rectangle getBounds();
		
		//TODO: Convert so the indexed de-reference is the principle one
		public Object get(int idx) {
			try {return get(PROTOTYPE.get(idx));} 
			catch (IndexOutOfBoundsException e) {throw new TupleBoundsException(idx, size());}
		}
		
		public Object get(String name) throws InvalidNameException {
			Rectangle bounds = getBounds();
			
			try {
				CanvasAttribute ename = CanvasAttribute.valueOf(name);
	
				switch (ename) {
					case BACKGROUND_COLOR: return getComponent().getBackground();
					case IMPLANTATION: return CANVAS_IMPLANTATION;
					case X: return bounds.getX();
					case Y: return bounds.getY();
					case WIDTH: return bounds.getWidth();
					case HEIGHT: return bounds.getHeight();
					default: return null;
				}
			} catch (Exception e) {
				if (name.equals("RIGHT")) {return bounds.getX() + bounds.getWidth();}
				if (name.equals("BOTTOM")) {return bounds.getY() + bounds.getHeight();}			
			}
			throw new InvalidNameException(name);
		}
		
		public double getX() {return getBounds().getX();}
		public double getY() {return getBounds().getY();}
		public double getWidth() {return getBounds().getWidth();}
		public double getHeight() {return getBounds().getHeight();}

		public int size() {return PROTOTYPE.size();}
		public List<String> getPrototype() {return PROTOTYPE;}
		
		public String toString() {return stencil.tuple.Tuples.toString(this);}
		
		/**Gets the default value for the named property.
		 * If the named property has no defined default, it is assumed to be 'null'.
		 *
		 * @param name Property to look up default value of.
		 * @return Default value of property.
		 */
		public boolean isDefault(String name, Object value) {
			if (PROTOTYPE.contains(name)) {return false;}
			Object def = CanvasAttribute.valueOf(name).defaultValue;
			return def == value || (def != null && def.equals(value));
		}
	}			

	
	/**Return the actual backing component*/
	public Component getComponent();

	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();

}
