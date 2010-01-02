package stencil.util.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class ArrayUtil {
	private ArrayUtil() {/*Not instantiable, utility class.*/}
	
	/**Append the given elements to the passed array.*/
	public static <T> T[] arrayAppend(T[] a, T...ts ) {
		T[] result = (T[]) java.lang.reflect.Array.
        newInstance(a.getClass().getComponentType(), a.length + ts.length);
		System.arraycopy(a, 0, result, 0, a.length);
	    System.arraycopy(ts, 0, result, a.length, ts.length);
	    return result;
	}

	
	/**List implementation backed by an array that cannot be re-sized.
	 * This IS NOT an immutable, elements may still be set.  However, it
	 * cannot be grown/shrunk. 
	 * 
	 * WARNING: ToArray returns the exact array passed in, instead of a copy.  
	 * This means this violates the contract of 'collection.'  
	 **/
	public static final class ArrayToList<T> implements List<T>{
		protected final T[] source;

		public ArrayToList(T[] source) {this.source =source;}

		public int size() {return source.length;}

		/** WARNING: ToArray also the exact array passed in, instead of a copy.  
		 * This means this violates the contract of 'collection.'
		 */  
		public Object[] toArray() {return source;}

		public boolean contains(Object o) {
			for (T v: source) {if (o.equals(v)) {return true;}}
			return false;
		}

		public boolean containsAll(Collection<?> c) {
			for (Object v: c) {if (!contains(v)) {return false;}}
			return true;
		}

		public T get(int index) {return source[index];}

		public T set(int index, T element) {
			T prior = source[index];
			source[index] = element;
			return prior;
		}

		public int indexOf(Object o) {return -1;}

		public boolean isEmpty() {return source.length >0;}

		public Iterator<T> iterator() {
			return new Iterator() {
				private int idx=0;
				public boolean hasNext() {return idx < source.length;}

				public Object next() {return source[idx++];}

				public void remove() {throw new UnsupportedOperationException();}
			};
		}

		public int lastIndexOf(Object o) {
			int last = -1;
			
			for (int i=0; i< source.length; i++) {
				if (o.equals(source[i])) {last = i;}
			}
			return last;
		}

		public List<T> subList(int fromIndex, int toIndex) {
			T[] t = (T[]) Array.newInstance(source.getClass(), toIndex-fromIndex);
			System.arraycopy(source, fromIndex, t, 0, t.length);
			return new ArrayToList(t);
		}

		
		public ListIterator<T> listIterator() {throw new UnsupportedOperationException();}
		public ListIterator<T> listIterator(int index) {throw new UnsupportedOperationException();}


		public <V> V[] toArray(V[] a) {throw new UnsupportedOperationException();}

		public boolean add(T o) {throw new UnsupportedOperationException();}
		public void add(int index, T element) {throw new UnsupportedOperationException();}
		public boolean addAll(Collection<? extends T> c) {throw new UnsupportedOperationException();}
		public boolean addAll(int index, Collection<? extends T> c) {throw new UnsupportedOperationException();}
		public void clear() {throw new UnsupportedOperationException();}

		public boolean remove(Object o) {throw new UnsupportedOperationException();}
		public T remove(int index) {throw new UnsupportedOperationException();}
		public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
		public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}

	}
}
