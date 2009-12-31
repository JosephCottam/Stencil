package stencil.tuple.prototype;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.Tuple;

public final class TuplePrototypes {
	private TuplePrototypes() {/*Utility class; should not be instantiated.*/}

	/**Create a list of default names.  Default names are derived
	 * from the prefix in a consistent manner.  If the prefix is null,
	 * the global tuple-default-key is used instead.
	 * 
	 * @param count
	 * @param prefix
	 * @return
	 */
	public static List<String> defaultNames(int count, String prefix) {
		if (prefix == null) {prefix = Tuple.DEFAULT_KEY;}
		String[] names= new String[count];
		names[0]=prefix;
		for (int i=1; i< count; i++) {
			names[i] = prefix + i;
		}
		return Arrays.asList(names);
	}

	/**Extract a list of the field names.*/
	public static List<String> getNames(Tuple tuple) {return getNames(tuple.getPrototype());}
	public static List<String> getNames(TuplePrototype prototype) {
		String[] s = new String[prototype.size()];
		for (int i=0; i<s.length; i++) {
			s[i] = prototype.get(i).getFieldName();
		}
		return Arrays.asList(s);
	}

	public static List<String> getNames(List<? extends TupleFieldDef> defs) {
		String[] c = new String[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).getFieldName(); 
		}
		return Arrays.asList(c);
	}

	
	/**Extract a list of the field types from the given prototype.*/
	public static List<Class> getTypes(Tuple tuple) {return getTypes(tuple.getPrototype());}
	public static List<Class> getTypes(TuplePrototype prototype) {
		Class[] s = new Class[prototype.size()];
		for (int i=0; i<s.length; i++) {
			s[i] = prototype.get(i).getFieldType();
		}
		return Arrays.asList(s);
	}
	
	public static List<Class> getTypes(List<? extends TupleFieldDef> defs) {
		Class[] c = new Class[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).getFieldType(); 
		}
		return Arrays.asList(c);
	}
	

	public static List<Class> defaultTypes(int size) {
		Class[] types = new Class[size];
		Arrays.fill(types, Object.class);
		return Arrays.asList(types);
	}

}
