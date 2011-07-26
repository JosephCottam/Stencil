package stencil.interpreter.tree;

import java.util.*;

import stencil.tuple.prototype.TuplePrototype;

public class TargetTuple implements Iterable<TupleField> {
	private final TupleField[] fields;
	private final TuplePrototype proto;
	
	public TargetTuple(TupleField[] fields) {
		this.fields = fields;
		
		//TODO: Modify to reflect the nested nature that I really want...
		String[] names = new String[fields.length];
		for (int i=0; i<names.length; i++) {names[i] = fields[i].toString();}
		proto = new TuplePrototype(names);
	}
	
	public TupleField[] fields() {return fields;}
	public int size() {return fields.length;}
	public Iterator<TupleField> iterator() {return Arrays.asList(fields).iterator();}
	public boolean contains(TupleField field) {return contains(field.toString());}
	public boolean contains(String field) {
		for(TupleField candidate: fields) {
			if (field.equals(candidate.toString())) {return true;}
		}
		return false;
	}
	
	public TuplePrototype asPrototype() {return proto;}
}
