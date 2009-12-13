package stencil.tuple.prototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimplePrototype extends ArrayList<SimpleFieldDef> implements TuplePrototype<SimpleFieldDef> {
	public SimplePrototype() {super();}

	public SimplePrototype(Collection<String> names) {
		this(new ArrayList(names), TuplePrototypes.defaultTypes(names.size()));
	}

	public SimplePrototype(String[] names, Class[] types) {this(Arrays.asList(names), Arrays.asList(types));}
	public SimplePrototype(List<String> names, List<Class> types) {
		super();

		assert names.size() == types.size() : "Must supply the same number of names as types: ";
		
		int count = names.size();
		for (int i=0; i < count; i++) {
			SimpleFieldDef d = new SimpleFieldDef(names.get(i), types.get(i));
			this.add(d);
		}
		validateNames();
	}

	/**Ensure there are no duplicate names.*/
	protected void validateNames() {
		Set<String> fields = new HashSet();
		for (TupleFieldDef def: this) {
			String name = def.getFieldName();
			if (fields.contains(name)) {
				throw new IllegalArgumentException("Cannot have two fields with the same name: " + Arrays.deepToString(TuplePrototypes.getNames(this).toArray()));
			}
			fields.add(name);
		}
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
