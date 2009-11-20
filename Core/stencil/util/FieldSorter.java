package stencil.util;

import java.util.Comparator;

import stencil.tuple.Tuple;

/**Sorts tuples based on a given field.
 * 
 * Field is sorted according to the passed comparator,
 * or, if no comparator is supplied, by assuming the field values are comparable and using the natural ordering.
 * 
 * @author jcottam
 *
 */
public class FieldSorter implements Comparator<Tuple> {
	private final String field;
	private final Comparator base;
	
	public FieldSorter(String field) {this(field, null);}
	public FieldSorter(String field, Comparator base) {
		this.field = field;
		this.base = base;
	}
	
	public int compare(Tuple t1, Tuple t2) {
		Object o1 = t1.get(field);
		Object o2 = t2.get(field);
		
		if (base != null) {return base.compare(o1, o2);}
		else {return ((Comparable) o1).compareTo(o2);}
	}
}
