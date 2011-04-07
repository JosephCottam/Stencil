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
package stencil.adapters.java2D.data.guides;

import java.util.Collection;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.*;

import stencil.adapters.java2D.data.Glyph2D;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.SpecializerTuple;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;

public abstract class GuideUtils {
	public static final String SAMPLE_KEY="sample";
	
	/**Applies the values found in a specializer map to the target.
	 * Only values that start with the given prefix (plus a name separator) will be applied.
	 */
	public static final <T extends Glyph2D> T applyDefaults(Specializer source, String prefix, T target) {
		Tuple update = Tuples.sift(prefix, new SpecializerTuple(source));		
		return (T) target.update(update);
	}
		
	/**Sets visible instance variables on the target if the variable name
	 * appears in the source specializer.
	 */
	public static final void setValues(Specializer source, Object target) {
		Class clss = target.getClass();
		
		for (String name: source.keySet()) {
			if (name.indexOf(NAME_SEPARATOR) >0) {continue;} //Skip compound names, won't match anyway
			
			try {
				Field f = clss.getField(name);
				f.set(target, Converter.convert(source.get(name), f.getType()));
			} catch (NoSuchFieldException e) {//ignored so other properties for the sampler can be included...
			} catch (SecurityException e) {System.err.println("Attempt to set value that cannot be securely accessed on " + target.toString());
			} catch (IllegalAccessException e) {System.err.println("Attempt to set value that cannot be accesssed on " + target.toString());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Error setting property: " + name, e);
			}			

		}
	}
	
	
	/**Given a collection of glyphs, what is a bounding box that contains them?*/
	public static final Rectangle2D fullBounds(Collection<Glyph2D> glyphs) {
		Rectangle2D bounds =  null;
		for (Glyph2D g: glyphs) {
			if (bounds == null) {
				bounds = (Rectangle2D) g.getBoundsReference().clone();
			} else {
				bounds.add(g.getBoundsReference());
			}
		}
		
		if (bounds == null) {bounds = new Rectangle2D.Double();}		
		return bounds;
	}

 }
