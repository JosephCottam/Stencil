package stencil.util.collections;

import java.util.Collection;

public final class Util {
	private Util() {/*Utility class. Not instantiable.*/}
	
	/**Does collection target contain any of the elements listed in collection candidates?*/
	public static final boolean containsAny(Collection target, Collection candidates) {
		for (Object c:candidates) {
			if (target.contains(c)) {return true;}
		}
		return false;
	}
}
