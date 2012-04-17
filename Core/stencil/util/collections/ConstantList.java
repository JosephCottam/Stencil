package stencil.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**List that always returns the same value.  Length can be set,
 * but only one value actually exists in the list.
 */
public class ConstantList<E> implements java.util.List<E> {
	protected final int size;
	protected final E value;
	
	public ConstantList(E value, int size) {
		this.value = value;
		this.size = size;
	}
	
	@Override
	public int size() {return size;}
	@Override
	public E get(int index) {
		if (index < size) {return value;}
		else {throw new RuntimeException("Out of bounds: " + index);}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int idx =0;
			@Override
			public boolean hasNext() {return idx< size;}
			@Override
			public E next() {
				if (idx == size) {throw new NoSuchElementException();}
				idx++; return value;
			}
			@Override
			public void remove() {throw new UnsupportedOperationException();}
		};
	}
	@Override
	public boolean isEmpty() {return size() == 0;}

	@Override
	public int indexOf(Object o) {
		if (contains(o)) {return 0;}
		else {return -1;}
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (contains(o)) {return size-1;}
		else {return -1;}
	}	

	@Override
	public List<E> subList(int fromIndex, int toIndex) {return new ConstantList(value, toIndex-fromIndex);}

	@Override
	public boolean contains(Object o) {return value.equals(o);}
	@Override
	public boolean containsAll(Collection<?> c) {throw new UnsupportedOperationException();}	

	@Override
	public boolean remove(Object o) {throw new UnsupportedOperationException();}	
	@Override
	public E remove(int index) {throw new UnsupportedOperationException();}
	
	@Override
	public boolean add(E e) {throw new UnsupportedOperationException();}
	@Override
	public void add(int index, E element) {throw new UnsupportedOperationException();}
	@Override
	public boolean addAll(Collection<? extends E> c) {throw new UnsupportedOperationException();}
	@Override
	public boolean addAll(int index, Collection<? extends E> c){throw new UnsupportedOperationException();}
	@Override
	public void clear() {throw new UnsupportedOperationException();}

	@Override
	public ListIterator<E> listIterator() {throw new UnsupportedOperationException();}
	@Override
	public ListIterator<E> listIterator(int index) {throw new UnsupportedOperationException();}	
	@Override
	public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}	
	@Override
	public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
	@Override
	public E set(int index, E element) {throw new UnsupportedOperationException();}

	@Override
	public Object[] toArray() {throw new UnsupportedOperationException();}	
	@Override
	public <T> T[] toArray(T[] a) {throw new UnsupportedOperationException();}
}
