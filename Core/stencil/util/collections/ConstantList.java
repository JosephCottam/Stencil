package stencil.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**List that always returns the same value.  Length can be set,
 * but only one value actually exists in the list.
 */
public class ConstantList<E> implements java.util.List<E> {
	private final int size;
	private final E value;
	
	public ConstantList(E value, int size) {
		this.value = value;
		this.size = size;
	}
	
	public int size() {return size;}
	public E get(int index) {
		if (index < size) {return value;}
		else {throw new RuntimeException("Out of bounds: " + index);}
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int idx =0;
			public boolean hasNext() {return idx< size;}
			public E next() {idx++; return value;}
			public void remove() {throw new UnsupportedOperationException();}
		};
	}
	public boolean isEmpty() {return size() == 0;}

	public int indexOf(Object o) {
		if (contains(o)) {return 0;}
		else {return -1;}
	}
	
	public int lastIndexOf(Object o) {
		if (contains(o)) {return size-1;}
		else {return -1;}
	}	

	public List<E> subList(int fromIndex, int toIndex) {return new ConstantList(value, toIndex-fromIndex);}

	public boolean contains(Object o) {return value.equals(o);}
	public boolean containsAll(Collection<?> c) {throw new UnsupportedOperationException();}	

	public boolean remove(Object o) {throw new UnsupportedOperationException();}	
	public E remove(int index) {throw new UnsupportedOperationException();}
	
	public boolean add(E e) {throw new UnsupportedOperationException();}
	public void add(int index, E element) {throw new UnsupportedOperationException();}
	public boolean addAll(Collection<? extends E> c) {throw new UnsupportedOperationException();}
	public boolean addAll(int index, Collection<? extends E> c){throw new UnsupportedOperationException();}
	public void clear() {throw new UnsupportedOperationException();}

	public ListIterator<E> listIterator() {throw new UnsupportedOperationException();}
	public ListIterator<E> listIterator(int index) {throw new UnsupportedOperationException();}	
	public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}	
	public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
	public E set(int index, E element) {throw new UnsupportedOperationException();}

	public Object[] toArray() {throw new UnsupportedOperationException();}	
	public <T> T[] toArray(T[] a) {throw new UnsupportedOperationException();}
}
