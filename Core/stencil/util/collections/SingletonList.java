package stencil.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SingletonList<T> implements List {
	private final T value;
	
	public SingletonList(T v) {value = v;}
	
	@Override
	public boolean add(Object e) {throw new UnsupportedOperationException();}

	@Override
	public void add(int index, Object element) {throw new UnsupportedOperationException();}

	@Override
	public boolean addAll(Collection c) {throw new UnsupportedOperationException();}

	@Override
	public boolean addAll(int index, Collection c) {throw new UnsupportedOperationException();}

	@Override
	public void clear() {throw new UnsupportedOperationException();}

	@Override
	public boolean contains(Object o) {return value.equals(o);}

	@Override
	public boolean containsAll(Collection c) {return c.size() == 1  && contains(c.iterator().next());}

	@Override
	public Object get(int index) {
		if (index != 0) {return new IndexOutOfBoundsException();}
		else {return value;}
	}

	@Override
	public int indexOf(Object o) {
		if (contains(o)) {return 0;}
		return -1;
	}

	@Override
	public boolean isEmpty() {return false;}

	@Override
	public Iterator iterator() {
		return new Iterator() {
			boolean hasNext = true;
			@Override
			public boolean hasNext() {return hasNext;}

			@Override
			public Object next() {
				hasNext = false;
				return value;
			}

			@Override
			public void remove() {throw new UnsupportedOperationException();}
		};
	}

	@Override
	public int lastIndexOf(Object o) {
		if (contains(o)) {return 0;}
		return -1;
	}

	@Override
	public ListIterator listIterator() {throw new UnsupportedOperationException();}

	@Override
	public ListIterator listIterator(int index) {throw new UnsupportedOperationException();}

	@Override
	public boolean remove(Object o) {throw new UnsupportedOperationException();}

	@Override
	public Object remove(int index) {throw new UnsupportedOperationException();}

	@Override
	public boolean removeAll(Collection c) {throw new UnsupportedOperationException();}

	@Override
	public boolean retainAll(Collection c) {throw new UnsupportedOperationException();}

	@Override
	public Object set(int index, Object element) {throw new UnsupportedOperationException();}

	@Override
	public int size() {return 1;}

	@Override
	public List subList(int fromIndex, int toIndex) {throw new UnsupportedOperationException();}

	@Override
	public Object[] toArray() {return new Object[]{value};}

	@Override
	public Object[] toArray(Object[] a) {throw new UnsupportedOperationException();}
}
