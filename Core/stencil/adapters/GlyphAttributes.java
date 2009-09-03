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
package stencil.adapters;

import stencil.adapters.general.Registrations.Registration;
import stencil.util.enums.Attribute;

/**Class to contain information about abstract glyphs*/

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class GlyphAttributes {
	/**Enumeration of common attributes, and accompanying information.
	 *
	 * These names and defaults are NOT intended to be the final word on names and
	 * defaults in the adapters.  Rather, these are the most common names and the
	 * most common defaults.  If an implantation has an attribute that corresponds to one
	 * in this list, it is strongly suggested that the attribute use the name given
	 * here.  Furthermore, if the default value given here is acceptable, then it also
	 * should be used.
	 *
	 * Conforming to the above guidelines helps keeps names and defaults consistent across adapters
	 * and implantations. The final word on implantation attributes and default values
	 * is specified by the adapter.
	 */
	public static enum StandardAttribute implements Attribute {
		FILL_COLOR 	(java.awt.Color.BLACK),
		X			(0.0d),
		Xn			(0.0d),
		Y			(0.0d),
		Yn			(0.0d),
		Z			(0.0d),
		HEIGHT		(0),
		WIDTH		(0),
		ID			(null, String.class),
		REGISTRATION (Registration.TOP_LEFT),
		IMPLANTATION (null, String.class),
		LAYERNAME	(null, String.class),
		ROTATION	(0d);

		private final Object defaultValue;
		private final Class type;

		StandardAttribute(Object defaultValue, Class type) {
			this.defaultValue = defaultValue;
			this.type = type;
		}
		StandardAttribute(Object defaultValue)  {
			this.defaultValue = defaultValue;
			this.type = defaultValue.getClass();
		}

		public Object getDefaultValue() {return defaultValue;}
		public Class getType() {return type;}
	}

	private GlyphAttributes() {/*Utility class, not instantiable.*/}
}