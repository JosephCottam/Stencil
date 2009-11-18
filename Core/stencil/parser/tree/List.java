package stencil.parser.tree;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

/**Supports the List node type, and read interface elements of java.util.List.*/
public class List<E extends StencilTree> extends StencilTree implements java.util.List<E> {
	
	/**Object to wrap an arbitrary tree in a java.util.List interface.
	 * Note, all indexes returned from this class are relative to the list, which
	 * may be offset from the list of children.  To convert from the list index to the
	 * root-child index, use toTree(int).
	 * */ 
	public static class WrapperList<T extends StencilTree> implements java.util.List<T> {
		protected Tree root; 
		protected int offset;
		
		/**@param root Which node's children are making a list?
		 * @param offset What is the first child that should be in the list? (Default is 0, indicating all children.)
		 */
		public WrapperList(Tree root, int offset) {
			this.root = root;
			this.offset = offset;
		}
		
		public WrapperList(Tree root) {this(root, 0);}
		
		/**Given a list index, return the tree-child index.*/
		private int toTree(int idx) {return idx+offset;}
		
		public boolean contains(Object o) {
			if (!(o instanceof Tree)) {return containsValue(o);}
			Tree t = (Tree) o;
			for (T t2: this) {if (t.equals(t2)) {return true;}}
			return false;
		}
		
		public boolean containsValue(Object o) {
			for (T t: this) {
				if (t instanceof Value) {return ((Value) t).getValue().equals(o);}
			}
			return false;
		}

		public boolean containsAll(Collection<?> c) {
			boolean contains = true;
			for (Object o: c) {contains = contains && contains(o);}
			return contains;
		}

		public T get(int index) {return (T) root.getChild(toTree(index));}

		public int indexOf(Object o) {
			if (o== null) {return -1;}

			for (int i=offset; i < root.getChildCount(); i++) {
				Tree t = root.getChild(i);
				if (o.equals(t)) {return i-offset;}
			}
			return -1;
		}

		public boolean isEmpty() {return (root.getChildCount()-offset)==0;}

		public Iterator<T> iterator() {
			return new Iterator() {
				int idx=offset;
				final int max = root.getChildCount();
				public boolean hasNext() {return idx < max;}
				public Object next() {return root.getChild(idx++);}
				public void remove() {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
			};

		}

		public int lastIndexOf(Object o) {
			if (o== null) {return -1;}

			for (int i = root.getChildCount()-1; i!=offset; i--) {
				Tree t = root.getChild(i);
				if (o.equals(t)) {return i-offset;}
			}
			return -1;
		}


		public int size() {return root.getChildCount();}

		public java.util.List<T> subList(int fromIndex, int toIndex) {
			ArrayList<T> children = new ArrayList(toIndex - fromIndex);
			for (int i=0; i< children.size(); i++) {
				children.set(i, get(fromIndex+toTree(i)));
			}
			return children;
		}

		public Object[] toArray() {
			Object[] children = new Object[root.getChildCount()-offset];
			return copyInto(children);
		}
		
		public <S> S[] toArray(S[] a) {
			if (a.length < size()) {a = (S[]) Array.newInstance(get(0).getClass(), size());}
			if (a.length > size()) {a[size()] = null;}
			
			return (S[]) copyInto(a);
		}
		
		private Object[] copyInto(Object[] target) {	
			for (int i=0; i< size(); i++) {
				target[i] = root.getChild(toTree(i));
			}
			return target;
		}
		
		public ListIterator<T> listIterator() {throw new UnsupportedOperationException("Cannot modify a Stencil Tree List, thus ListIterator not supported.");}
		public ListIterator<T> listIterator(int index) {throw new UnsupportedOperationException("Cannot modify a Stencil Tree List, thus ListIterator not supported.");}

		public boolean add(T o) {throw new UnsupportedOperationException("Cannot add to a Stencil Tree List.");}
		public void add(int index, T element) {throw new UnsupportedOperationException("Cannot add to a Stencil Tree List.");}
		public boolean addAll(Collection<? extends T> c) {throw new UnsupportedOperationException("Cannot add to a Stencil Tree List.");}
		public boolean addAll(int index, Collection<? extends T> c) {throw new UnsupportedOperationException("Cannot add to a Stencil Tree List.");}

		public void clear() {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
		public boolean remove(Object o) {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
		public T remove(int index) {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
		public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
		public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
		public T set(int index, T element) {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}
	}
	
	protected WrapperList<E> base;
	
	public List(Token token) {
		super(token);
		base = new WrapperList<E>(this);
	}

	public boolean add(E o) {return base.add(o);}
	public void add(int index, E element) {base.add(index, element);}
	public boolean addAll(Collection<? extends E> c) {return base.addAll(c);}
	public boolean addAll(int index, Collection<? extends E> c) {return base.addAll(index, c);}
	public void clear() {base.clear();}
	public boolean contains(Object o) {return base.contains(o);}
	public boolean containsAll(Collection<?> c) {return base.containsAll(c);}
	public E get(int index) {return base.get(index);}
	public int indexOf(Object o) {return base.indexOf(o);}
	public boolean isEmpty() {return base.isEmpty();}
	public Iterator<E> iterator() {return base.iterator();}
	public int lastIndexOf(Object o) {return base.lastIndexOf(o);}
	public ListIterator<E> listIterator() {return base.listIterator();}
	public ListIterator<E> listIterator(int index) {return base.listIterator(index);}
	public boolean remove(Object o) {return base.remove(o);}
	public E remove(int index) {return base.remove(index);}
	public boolean removeAll(Collection<?> c) {return base.removeAll(c);}
	public boolean retainAll(Collection<?> c) {return base.retainAll(c);}
	public E set(int index, E element) {return base.set(index,element);}
	public int size() {return base.size();}
	public java.util.List<E> subList(int fromIndex, int toIndex) {return base.subList(fromIndex, toIndex);}
	public Object[] toArray() {return base.toArray();}
	public <T> T[] toArray(T[] a) {return base.toArray(a);}

}
