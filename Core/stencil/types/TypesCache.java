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
package stencil.types;

import java.util.Map;
import java.util.HashMap;

/**
 * Mediates the conversion of internal and external type representations.
 * 
 */
public final class TypesCache {
	private static final Map<String, SigilType> names = new HashMap<String, SigilType>();
	private static final Map<Class, SigilType> externals = new HashMap<Class, SigilType>();
	private static final Map<Class, SigilType> internals = new HashMap<Class,SigilType>();

	
	public static final class TypeNotFound extends RuntimeException {
		TypeNotFound(String name) {super("Type of name " + name + " not found.");}
		TypeNotFound(Class clss) {super("Type not found for class " + clss);}
		TypeNotFound(Class clss, boolean internal) {
			super(String.format("Type not found for %1$s class %2$s.", internal?"internal":"external", clss.getName()));
		}
	}
	
	static {
		SigilType t = new stencil.types.color.Color();
		
		names.put("@color", t);
		externals.put(java.awt.Color.class, t);
		externals.put(java.awt.Paint.class, t);
		internals.put(stencil.types.color.ColorTuple.class, t);
	}

	public static boolean hasTypeFor(Object value)  {return hasTypeFor(value.getClass());}
	public static boolean hasTypeFor(String name) 	{return names.containsKey(name);}
	public static boolean hasTypeFor(Class clss) 	{return internals.containsKey(clss) || externals.containsKey(clss);}
	
	public static SigilType getType(String name) {
		if (!names.containsKey(name)) {throw new TypeNotFound(name);}
		return names.get(name);
	}
	
	
	public static SigilType getType(Object value) {return getType(value.getClass());}
	public static SigilType getType(Class clss) {
		if (internals.containsKey(clss)) {return internals.get(clss);}
		if (externals.containsKey(clss)) {return externals.get(clss);}
		throw new TypeNotFound(clss);
	}
	
	public static SigilType getTypeByInternal(Class clss) {
		if (!internals.containsKey(clss)) {throw new TypeNotFound(clss, true);}
		return internals.get(clss);
	}
	
	public static SigilType getTypeByExternal(Class clss) {
		if (!externals.containsKey(clss)) {throw new TypeNotFound(clss, false);}
		return externals.get(clss);
	}
	
}
