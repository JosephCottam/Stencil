package stencil.tuple.prototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePrototype extends ArrayList<SimpleFieldDef> implements TuplePrototype<SimpleFieldDef> {
	public SimplePrototype() {super();}


	public SimplePrototype(String... names) {this(Arrays.asList(names));}
	public SimplePrototype(List<String> names) {
		this(names.toArray(new String[names.size()]), TuplePrototypes.defaultTypes(names.size()));
	}

	public SimplePrototype(String[] names, Class[] types) {this(Arrays.asList(names), Arrays.asList(types));}
	public SimplePrototype(List<String> names, List<Class> types) {
		super();
		assert names != null : "Must supply names";
		assert types != null : "Must supply types";
		assert names.size() == types.size() : "Must supply the same number of names as types: ";
		
		int count = names.size();
		for (int i=0; i < count; i++) {
			SimpleFieldDef d = new SimpleFieldDef(names.get(i), types.get(i));
			this.add(d);
		}
		assert validateNames() : "Name validation ended in exception.";
	}

	/**Ensure there are no duplicate names.*/
	protected boolean validateNames() {
		Set<String> fields = new HashSet();
		for (TupleFieldDef def: this) {
			String name = def.getFieldName();
			if (!fields.add(name)) {
				String names = Arrays.deepToString(TuplePrototypes.getNames((TuplePrototype) this));
				String message = String.format("Attempt to add %1$s, a duplicate name (all names: %2$s).", name, names);
				throw new IllegalArgumentException(message);
			}
		}
		return true;
	}

	public boolean contains(String name) {
		return indexOf(name) >= 0;
	}

	public int indexOf(String name) {
		int idx =0;
		for (TupleFieldDef d: this) {
			if (name.equals(d.getFieldName())) {return idx;}
			idx++;
		}
		return -1;
	}
	
}
