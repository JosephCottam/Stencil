package stencil.util.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
public final class ArrayUtil {
	private ArrayUtil() {/*Not instantiable, utility class.*/}
	
	/**Append the given elements to the passed array.*/
	public static <T> T[] arrayAppend(T[] a, T...ts ) {
		T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), a.length + ts.length);
		System.arraycopy(a, 0, result, 0, a.length);
	    System.arraycopy(ts, 0, result, a.length, ts.length);
	    return result;
	}

	/**The index of the element in the list or -1 if it is not contained
	 * in the list.  This should be used on unsorted lists (Arrays.binarySearch is much
	 * faster for sorted lists of any size).
	 */
	public static <T> int indexOf(T element, T[] values) {
		for (int i=0; i< values.length;i++) {
			if (element == values[i] || (element != null && element.equals(values[i]))) {return i;}
		}
		return -1;
	}

	public static <T> int indexOf(T element, Object values) {
		for (int i=0; i< Array.getLength(values); i++) {
			if (element == Array.get(values, i)) {return i;}
		}
		return -1;
	}
	
	/**Move elements from the iterator to the passed array. 
	 * Will stop when the array is full or the iterator is exhausted.
	 * Return value is the passed array.
	 */
	public static <T> T[] fromIterator(Iterable<T> source, T[] target) {
		Iterator<T> it = source.iterator();
		for (int i=0; i< target.length && it.hasNext(); i++) {
			target[i] = it.next();
		}
		return target;
	}
	

	/**Deep to string for the primtive int type.**/
	public static final String deepToString(int[] ids) {
		StringBuilder b = new StringBuilder("[");
		for (int i: ids) {b.append(i); b.append(", ");}
		b.deleteCharAt(b.length()-1);
		b.append("]");
		return b.toString();
	}
	
	public static final String prettyString(Iterable entries) {
		StringBuilder b = new StringBuilder();
		for (Object entry: entries) {
			b.append(entry.toString());
			b.append(", ");
		}
		if (b.length() >0) {
			b.deleteCharAt(b.length()-1);
			b.deleteCharAt(b.length()-1);
		}
		return b.toString();
	}
	
	/**String formatted for error messages.**/
	public static final String prettyString(Object[] entries) {return prettyString(Arrays.asList(entries));}
	
	public static int[] intArray(List<Integer> values) {
		final int[] ints = new int[values.size()];
		for (int i=0 ;i < ints.length; i++) {ints[i] = values.get(i).intValue();}
		return ints;
		
	}
}
