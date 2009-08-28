package stencil.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Set where values are kept conditionally, and
 * periodically removed.
 * 
 * @param <T> Type of object being kept
 * @param <M> Type of mark being used to track conditions
 */
public abstract class ConditionSet<T,M> implements Set<T>{

	protected class Entry{
		public M mark;
		public T value;

		public Entry(M mark, T value) {
			this.mark = mark;
			this.value = value;
		}

		public boolean equals(Object o) {
			if (!(o.getClass().equals(Entry.class))) {return false;}
			Entry e = (Entry) o;
			return (this == e || value.equals(e.value));
		}

		public int hashCode() {return value.hashCode();}
	}

	protected Set<Entry> contents = new HashSet();

	public abstract void sweep();
	public abstract Entry wrap(T o);


	public boolean add(T o) {return contents.add(wrap(o));}

	public boolean addAll(Collection<? extends T> c) {
		boolean rv = false;
		for (T o: c) {rv = rv || add(o);}
		return rv;
	}

	public void clear() {contents.clear();}

	public boolean contains(Object o) {
		Entry comp = wrap((T) o);
		return contents.contains(comp);
	}
	public boolean containsAll(Collection<?> c) {return contents.containsAll(c);}

	public boolean isEmpty() {return contents.isEmpty();}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Iterator<Entry> base = contents.iterator();
			public boolean hasNext() {return base.hasNext();}
			public T next() {return base.next().value;}
			public void remove() {base.remove();}
		};
	}

	public boolean remove(Object o) {return contents.remove(o);}

	public boolean removeAll(Collection<?> c) {
		boolean rv = false;
		for (Object o: c) {rv = rv || remove(o);}
		return rv;
	}

	public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}

	public int size() {return contents.size();}

	public Object[] toArray() {
		Object[] os = new Object[contents.size()];

		Iterator<Entry> e = contents.iterator();
		for (int i=0; e.hasNext(); i++) {
			os[i] = e.next().value;
		}
		return os;
	}

	public <S> S[] toArray(S[] a) {
		Object[] os = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), contents.size());

		Iterator<Entry> e = contents.iterator();
		for (int i=0; e.hasNext(); i++) {
			os[i] = e.next().value;
		}
		return (S[]) os;
	}


}
