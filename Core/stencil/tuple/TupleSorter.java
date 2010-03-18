package stencil.tuple;

import java.util.Comparator;

/**Sorts tuples based upon a key field.
 * Tuples must have matching field indexes for the field that
 * is to be the basis of comparison, but no other field needs to match.
 * A base comparitor may be supplied to compare the actual values
 * encountered.  If no comparitor is used AND the values
 * implements comparable, the value's comaprable will be used.
 * If no comparitor is provided and the classes are not comparable,
 * the values are judged to be equal.
 * 
 *
 * 
 * @author jcottam
 *
 */
public class TupleSorter implements Comparator<Tuple> {
	private final int field;
	private final Comparator base;

	public TupleSorter(int field) {this(field, null);}
	
	public TupleSorter(int field, Comparator base) {
		this.field= field;
		this.base=base;
	}
	
	public int compare(Tuple o1, Tuple o2) {
		Object value1 = o1.get(field);
		Object value2 = o2.get(field);
		
		if (base != null) {
			return base.compare(value1, value2);
		} else if (value1 instanceof Comparable && value1.getClass().equals(value2.getClass())) {
			return ((Comparable) value1).compareTo(value2);
		} else {
			return 0;
		}
		
	}

}
