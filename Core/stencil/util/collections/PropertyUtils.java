package stencil.util.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtils {
	/**Returns a list of keys that have the given prefix.*/
	public static List<String> filter(Properties props, String prefix) {
		List<String> results = new ArrayList();
		
		for (Object ky: props.keySet()) {
			String key = (String) ky;
			if (key.startsWith(prefix)) {
				results.add(key);
			}
		}
		return results;
	}
	
}
