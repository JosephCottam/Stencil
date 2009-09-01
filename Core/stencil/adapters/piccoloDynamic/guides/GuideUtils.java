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
package stencil.adapters.piccoloDynamic.guides;

import java.util.Map;
import java.lang.reflect.*;

import stencil.adapters.piccoloDynamic.NodeTuple;
import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.NAME_SEPARATOR_PATTERN;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;

public abstract class GuideUtils {
	
	/**Applies the values found in a specializer map to the target.
	 * Only values that start with the given prefix (plus a name separator) will be applied.
	 */
	public static final void applyDefaults(Specializer source, String prefix, NodeTuple target) {
		for (String name: source.getMap().keySet()) {
			if (!name.startsWith(prefix)) {continue;}
			String rootName = getRootName(name);
			target.set(rootName, source.getMap().get(name));
		}
	}
	
	private static final String getRootName(final String name) {
		try {return name.split(NAME_SEPARATOR_PATTERN)[1];}
		catch (Exception e) {throw new RuntimeException("Could not find root name in " + name, e);}
	}
	
	/**Sets visible instance variables on the target if the variable name
	 * appears in the source specializer.
	 */
	public static final void setValues(Specializer source, Object target) {
		Map<String, Atom> map = source.getMap();
		Class clss = target.getClass();
		NodeTuple wrapper = null;
		
		for (String name: map.keySet()) {
			if (name.indexOf(NAME_SEPARATOR) >0) {continue;} //Skip compound names, won't match anyway
			
			try {
				Field f = clss.getField(name);
				f.set(target, Converter.convert(map.get(name), f.getType()));
			} catch (SecurityException e) {System.err.println("Attempt to set value that cannot be securely accessed on " + target.toString());
			} catch (IllegalAccessException e) {System.err.println("Attempt to set value that cannot be accesssed on " + target.toString());
			} catch (NoSuchFieldException e) {
				if (wrapper == null && target instanceof Node) {wrapper = new  NodeTuple((Node) target);}
				
				if (wrapper != null) {
					try {
						wrapper.set(name, map.get(name));
					} catch (Exception ex) {
						throw new RuntimeException("Error setting property: " + name, ex);
					}
				} else {
					throw new RuntimeException("Property not known: " + name);
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Error setting property: " + name, e);
			}			

		}
	}
 }
