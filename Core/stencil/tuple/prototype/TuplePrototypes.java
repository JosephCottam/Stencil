package stencil.tuple.prototype;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.Tuple;

public final class TuplePrototypes {
	public static final TuplePrototype EMPTY_PROTOTYPE = new SimplePrototype();

	
	private TuplePrototypes() {/*Utility class; should not be instantiated.*/}

	/**Create a list of default names.  Default names are derived
	 * from the prefix in a consistent manner.  If the prefix is null,
	 * the global tuple-default-key is used instead.
	 * 
	 * @param count
	 * @param prefix
	 * @return
	 */
	public static String[] defaultNames(int count, String prefix) {
		if (count ==0) {return new String[0];}
		if (prefix == null) {prefix = Tuple.DEFAULT_KEY;}

		String[] names= new String[count];
		names[0]=prefix;
		for (int i=1; i< count; i++) {
			names[i] = prefix + i;
		}
		return names;
	}

	/**Extract a list of the field names.*/
	public static String[] getNames(Tuple tuple) {return getNames(tuple.getPrototype());}
	public static String[] getNames(TuplePrototype prototype) {
		String[] s = new String[prototype.size()];
		for (int i=0; i<s.length; i++) {
			s[i] = prototype.get(i).getFieldName();
		}
		return s;
	}

	public static String[] getNames(List<? extends TupleFieldDef> defs) {
		String[] c = new String[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).getFieldName(); 
		}
		return c;
	}

	
	/**Extract a list of the field types from the given prototype.*/
	public static Class[] getTypes(Tuple tuple) {return getTypes(tuple.getPrototype());}
	public static Class[] getTypes(TuplePrototype prototype) {
		Class[] c = new Class[prototype.size()];
		for (int i=0; i<c.length; i++) {
			c[i] = prototype.get(i).getFieldType();
		}
		return c;
	}
	
	public static Class[] getTypes(List<? extends TupleFieldDef> defs) {
		Class[] c = new Class[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).getFieldType(); 
		}
		return c;
	}
	

	public static Class[] defaultTypes(int size) {
		Class[] types = new Class[size];
		Arrays.fill(types, Object.class);
		return types;
	}

}
