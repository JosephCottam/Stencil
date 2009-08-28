/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.util.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**List implementation backed by an array that cannot be re-sized.
 * This IS NOT an immutable, elements may still be set.  However, it
 * cannot be grown/shrunk. 
 * 
 * WARNING: ToArray also the exact array passed in, instead of a copy.  
 * This means this violates the contract of 'collection.'  
 **/
public final class ArrayToList<T> implements List<T>{
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
