package stencil.tuple.prototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TuplePrototype<T extends TupleFieldDef> implements Iterable<T> {
	private Integer hashCode;
	private final List<T> fields;
	
	public TuplePrototype() {
		fields = Collections.unmodifiableList(new ArrayList());
	}

	public TuplePrototype(String... names) {this(names, TuplePrototypes.defaultTypes(names.length));}
	public TuplePrototype(String[] names, Class[] types) {this(Arrays.asList(names), Arrays.asList(types));}
	public TuplePrototype(List<String> names, List<Class> types) {
		super();
		assert names != null : "Must supply names";
		assert types != null : "Must supply types";
		assert names.size() == types.size() : "Must supply the same number of names as types: ";
		
		List<T> fields = new  ArrayList();
		int count = names.size();
		for (int i=0; i < count; i++) {
			SimpleFieldDef d = new SimpleFieldDef(names.get(i).trim(), types.get(i), null);
			fields.add((T) d);	//HACK: Remove this cast by turning this into a static factory method
		}
		this.fields = Collections.unmodifiableList(fields);
		validateNames();
	}
	
	public TuplePrototype(T... defs) {
		fields = Collections.unmodifiableList(Arrays.asList(defs));
	}
	
	protected TuplePrototype (TuplePrototype<T> base) {
		ArrayList fields = new ArrayList();
		fields.addAll(base.fields);
		this.fields = Collections.unmodifiableList(fields);
	}

	/**Ensure there are no duplicate names.*/
	protected boolean validateNames() {
		Set<String> fieldNames = new HashSet();
		for (TupleFieldDef def: fields) {
			String name = def.name();
			if (!fieldNames.add(name)) {
				String names = Arrays.deepToString(TuplePrototypes.getNames(this));
				String message = String.format("Attempt to add %1$s, a duplicate name (all names: %2$s).", name, names);
				throw new IllegalArgumentException(message);
			}
		}
		return true;
	}

	public boolean contains(String name) {
		return indexOf(name) >= 0;
	}

	public int indexOf(TupleFieldDef def) {
		int idx = indexOf(def.name());
		return (idx >=0 && fields.get(idx).type().isAssignableFrom(def.type())) ? idx : -1;	//The type relation might be wrong...not sure if it should be equal or super-class the other way
	}
	
	public int indexOf(String name) {
		int idx =0;
		for (TupleFieldDef d: fields) {
			if (name.equals(d.name())) {return idx;}
			idx++;
		}
		return -1;
	}
	
	
	public int hashCode() {
		if (hashCode == null) {
			int code =1;
			for (TupleFieldDef def: fields) {code = code*def.hashCode();}
			return code;
			
		}		
		return hashCode;
	}
	
	public int size() {return fields.size();}

	public T get(int i) {return fields.get(i);}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) {return true;}
		if (!(other instanceof TuplePrototype)) {return false;}
		TuplePrototype alter = (TuplePrototype) other;
		
		if (alter.size() != size()) {return false;}
		for (int i=0; i< size(); i++) {
			if (!(alter.get(i).equals(get(i)))) {return false;}
		}
		return true;
	}

	/**Check if the names of two prototypes are in the same order.**/
	public boolean nameEqual(TuplePrototype alter) {
		for (int i=0; i< size(); i++) {
			if (!alter.get(i).name().equals(get(i).name())) {return false;}
		}
		return true;
	}
	
	@Override
	public Iterator<T> iterator() {return fields.iterator();}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		for (T f: fields) {
			b.append(f.toString());
			b.append(",");
		}
		b.replace(b.length()-1, b.length(), ")");
		return b.toString();
	}
}