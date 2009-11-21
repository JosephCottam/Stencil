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

import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.tuple.MutableTuple;
import stencil.util.enums.Attribute;

/** The ViewTuple is the interface to a view
 * of the canvas.  To be complete, an adapter
 * must provide appropriate view tuples to manipulate
 * their camera-like objects and transition between
 * application/screen, canvas and view coordinate spaces.
 *
 * ViewTuples do not set the width/height/X/Y of the panel,
 * rather they manipulate the zoom or pan levels displayed
 * in that panel.
 *
 * Due to graphics system constraints, the width and height may not
 * be independently set-able. For adapters where this is not
 * possible, it is suggested that ZOOM be supported instead
 * and WIDTH/HEIGHT throw exceptions.  Systems capable of setting
 * independent X and Y zoom distortions may handle ZOOM as they see
 * fit. See the adapter implementation for details of its implementation.
 *
 * View tuple also provide basic abilities to convert between
 * the view and the canvas coordinate system.
 *
 * @author jcottam
 *
 */
public interface ViewTuple extends MutableTuple {
	public static final String VIEW_IMPLANTATION = "STENCIL_VIEW";

	public enum ViewAttribute implements Attribute{
		IMPLANTATION (null, String.class),
		ZOOM (0, Double.class),
		X (null,StandardAttribute.X.getType()),
		Y (null,StandardAttribute.Y.getType()),
		WIDTH (StandardAttribute.WIDTH.getDefaultValue(),StandardAttribute.WIDTH.getType()),
		HEIGHT (StandardAttribute.HEIGHT.getDefaultValue(),StandardAttribute.HEIGHT.getType()),
		PORTAL_WIDTH (0, Double.class),
		PORTAL_HEIGHT (0, Double.class);

		final Object defaultValue;
		final Class type;

		ViewAttribute(Object defaultValue, Class type)  {
			this.defaultValue = defaultValue;
			this.type = type;
		}

		public Class getType() {return type;}
		public Object getDefaultValue() {return defaultValue;}
	}

	public static abstract class Simple implements ViewTuple {
		protected static final List<String> PROTOTYPE;
		static {
			HashSet<String> s = new HashSet<String>();
			for (ViewAttribute a: EnumSet.allOf(ViewAttribute.class)) {s.add(a.name());}
			PROTOTYPE = Collections.unmodifiableList(new ArrayList(s));
		}
		
		public List<String> getPrototype() {return PROTOTYPE;}
		public int size() {return PROTOTYPE.size();}		
		/**Gets the default value for the named property.
		 * If the named property has no defined default, it is assumed to be 'null'.
		 *
		 * @param name Property to look up default value of.
		 * @return Default value of property.
		 */
		public boolean isDefault(String name, Object value) {
			if (PROTOTYPE.contains(name)) {return false;}
			Object def = ViewAttribute.valueOf(name).defaultValue;
			return def == value || (def != null && def.equals(value));
		}
		
		public String toString() {return stencil.tuple.Tuples.toString(this);}
	}
	
	/**Given a point in the canvas, where is it in the view?
	 * This method may return negative values in the points, indicating
	 * that the source point is not actually in the view right now.
	 */
	public Point2D canvasToView(Point2D p);

	/**Given a point in the view, what is the corresponding canvas coordinate?*/
	public Point2D viewToCanvas(Point2D p);

	/**Given a distance in the view, what is the corresponding distance in the canvas?*/
	public Dimension2D viewToCanvas(Dimension2D p);

	/**Given a distance in the canvas, what is the corresponding distance in the view?*/
	public Dimension2D canvasToView(Dimension2D p);

}
