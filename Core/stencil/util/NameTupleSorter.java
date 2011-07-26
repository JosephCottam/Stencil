package stencil.util;

import java.util.Comparator;

import stencil.tuple.PrototypedTuple;

/**Sorts tuples based on a given field.
 * 
 * Field is sorted according to the passed comparator,
 * or, if no comparator is supplied, by assuming the field values are comparable and using the natural ordering.
 * 
 * @author jcottam
 *
 */
public class NameTupleSorter implements Comparator<PrototypedTuple> {
	private final String field;
	private final Comparator base;
	
	public NameTupleSorter(String field) {this(field, null);}
	public NameTupleSorter(String field, Comparator base) {
		this.field = field;
		this.base = base;
	}
	
	public int compare(PrototypedTuple t1, PrototypedTuple t2) {
		Object o1 = t1.get(field);
		Object o2 = t2.get(field);
		
		if (base != null) {return base.compare(o1, o2);}
		else {return ((Comparable) o1).compareTo(o2);}
	}
}
