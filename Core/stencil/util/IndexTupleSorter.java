package stencil.util;

import java.util.Comparator;

import stencil.tuple.Tuple;

/**Sorts tuples based on a given field index.
 * 
 * Field is sorted according to the passed comparator,
 * or, if no comparator is supplied, by assuming the field values are comparable and using the natural ordering.
 * 
 * @author jcottam
 *
 */
public class IndexTupleSorter implements Comparator<Tuple> {
	private final int idx;
	private final Comparator base;
	
	public IndexTupleSorter(int idx) {this(idx, null);}
	public IndexTupleSorter(int idx, Comparator base) {
		this.idx = idx;
		this.base = base;
	}
	
	public int compare(Tuple t1, Tuple t2) {
		Object o1 = t1.get(idx);
		Object o2 = t2.get(idx);
		
		if (base != null) {return base.compare(o1, o2);}
		else {return ((Comparable) o1).compareTo(o2);}
	}
}
