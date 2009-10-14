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
package stencil.explore.model.sources;

import java.util.Map;
import java.util.HashMap;

/**Cache of source details.  This is used to preserve source information
 * across iterations of a stencil.  The stencil may actually be in an invalid
 * state, so the stream source definitions may be erased during editing from
 * the main store.  To recover the stream definitions, we use some heuristics
 * in conjunction with this cache to help restore the state automatically.
 */
public final class SourceCache {
	/**Storage by name and type.*/
	private static Map<String, StreamSource> cache = new HashMap();
	
	/**Storage by name only.*/
	private static Map<String, StreamSource> weakCache = new HashMap();


	public static boolean weakContains(String name) {return weakCache.containsKey(weakKey(name));}
	public static boolean weakContains(StreamSource prototype) {return weakCache.containsKey(weakKey(prototype));}

	public static StreamSource weakGet(String name) {return weakCache.get(weakKey(name));}
	public static StreamSource weakGet(StreamSource prototype) {return weakCache.get(weakKey(prototype));}

	public static boolean contains(StreamSource prototype) {return cache.containsKey(key(prototype));}
	public static StreamSource get(StreamSource prototype) {return cache.get(key(prototype));}

	/**Store the value.  Will only store the value weakly if it is not
	 * yet complete.  If it is complete, it stores it both weak and strong.
	 * @param value
	 */
	public static void put(StreamSource value) {
		if (value.isReady()) {cache.put(key(value), value);}
		weakCache.put(weakKey(value), value);
	}

	public static void clear() {
		cache.clear();
		weakCache.clear();
	}

	private static String key(StreamSource source) {return key(source.name, source.getClass());}
	private static String key(String name, Class type) {return name + type.getName();}

	private static String weakKey(StreamSource source) {return weakKey(source.name);}
	private static String weakKey(String name) {return name;}

}
