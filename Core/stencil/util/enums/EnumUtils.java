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
package stencil.util.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnumUtils {
	private static final Map<Class, List<String>> enumCache = new HashMap();
	
	/**The collection of 'is' functions return true if the value
	 * of this enum equals the passed key by either address comparison (==) or
	 * by comparing the enum name to the .toString() of the key.
	 * @param v
	 * @return
	 */
	public static boolean is(Enum first, Enum second) {return first == second;}
	public static boolean is(Enum ref, Object key) {return is(ref, key.toString());}
	public static boolean is(Enum ref, String key) {
		Object v;

		try {v = Enum.valueOf(ref.getDeclaringClass(), key);}
		catch (Exception e) {return false;}

		return v == ref;
	}

	/**Check if this enumeration contains a value with the given name.*/
	public static boolean contains(Enum source, String name) {return contains(source.getDeclaringClass(), name);}
	public static boolean contains(Class source, String name) {
		assert(Enum.class.isAssignableFrom(source));
		
		List<String> s;
		if (!enumCache.containsKey(source)) {
			s = allNames(source);
			enumCache.put(source, s);
		} else {
			s = enumCache.get(source);
		}
		
		return s.contains(name);
	}


	/**Get a list of all of the names in this enumeration.*/
	public static List<String> allNames(Enum source) {return allNames(source.getDeclaringClass());}
	public static List<String> allNames(Class source) {
		assert(Enum.class.isAssignableFrom(source));

		EnumSet<? extends Enum> searchSet = EnumSet.allOf(source);
		List<String> rv = new ArrayList<String>(searchSet.size());
		for (Enum a:searchSet) {rv.add(a.name());}
		return rv;
	}

	/**Returns a list of all of the names in a list of enums. Names may be
	 * duplicated if the names appear in more than one enum.*/
	public static List<String> allNames(Class<? extends Enum>... sources) {
		ArrayList<String> names = new ArrayList();
		for(Class<? extends Enum> source: sources) {
			names.addAll(EnumUtils.allNames(source));
		}
		return names;
	}
}