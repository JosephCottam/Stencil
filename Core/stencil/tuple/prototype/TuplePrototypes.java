package stencil.tuple.prototype;

import static stencil.parser.ParserConstants.NAME_SEPARATOR;
import static stencil.parser.ParserConstants.NAME_SEPARATOR_PATTERN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.util.collections.ArrayUtil;

public final class TuplePrototypes {
	public static final TuplePrototype EMPTY_PROTOTYPE = new TuplePrototype();

	
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
	public static String[] getNames(PrototypedTuple tuple) {return getNames(tuple.prototype());}
	public static String[] getNames(TuplePrototype prototype) {
		String[] s = new String[prototype.size()];
		for (int i=0; i<s.length; i++) {
			s[i] = prototype.get(i).name();
		}
		return s;
	}

	public static String[] getNames(List<? extends TupleFieldDef> defs) {
		String[] c = new String[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).name(); 
		}
		return c;
	}

	/**Formats names for printing (useful for error messages)**/
	public static String prettyNames(TuplePrototype proto) {return ArrayUtil.prettyString(getNames(proto));}
	
	
	/**Extract a list of the field types from the given prototype.*/
	public static Class[] getTypes(PrototypedTuple tuple) {return getTypes(tuple.prototype());}
	public static Class[] getTypes(TuplePrototype prototype) {
		Class[] c = new Class[prototype.size()];
		for (int i=0; i<c.length; i++) {
			c[i] = prototype.get(i).type();
		}
		return c;
	}
	
	public static Class[] getTypes(List<? extends TupleFieldDef> defs) {
		Class[] c = new Class[defs.size()];
		for (int i =0; i< c.length; i++) {
			c[i] = defs.get(i).type(); 
		}
		return c;
	}
	

	public static Class[] defaultTypes(int size) {
		Class[] types = new Class[size];
		Arrays.fill(types, Object.class);
		return types;
	}
	
	/**Appends the prototypes together.  Assumes that names are disjoint (does not do any shared-name checks)**/
	public static TuplePrototype append(final TuplePrototype... prototypes) {
		int total=0;
		for (TuplePrototype proto:prototypes) {total+=proto.size();}
		TupleFieldDef[] fields = new TupleFieldDef[total];
		
		int at=0;
		for (TuplePrototype proto: prototypes) {
			for (Object d: proto) {
				TupleFieldDef def = (TupleFieldDef) d;
				fields[at] = def;
				at++;
			}
		}
		return new TuplePrototype(fields);
	}
	
	public static TuplePrototype extend(TuplePrototype proto, TupleFieldDef... defs) {return append(proto, new TuplePrototype(defs));}
	public static TuplePrototype extend(TuplePrototype proto, List<? extends TupleFieldDef> defs) {
		return append(proto, new TuplePrototype(defs.toArray(new TupleFieldDef[defs.size()])));
	}
	
	
	public static final TuplePrototype sift(TuplePrototype<? extends TupleFieldDef> source, String prefix) {
		List<TupleFieldDef> defs = new ArrayList();
				
		for (TupleFieldDef field: source) {
			String[] parts = field.name().split(NAME_SEPARATOR_PATTERN);
			if (prefix == null && parts.length == 1) {
				defs.add(field);
			} else if (prefix != null && parts.length == 1) {
				continue;		//looking for a prefix, but none found, move along
			} else if (prefix != null && parts[0].equals(prefix)) {
				defs.add(field.rename(field.name().substring(field.name().indexOf(NAME_SEPARATOR)+1)));				
			}
		}
		return new TuplePrototype(defs.toArray(new TupleFieldDef[defs.size()]));
	}
}
