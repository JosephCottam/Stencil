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
