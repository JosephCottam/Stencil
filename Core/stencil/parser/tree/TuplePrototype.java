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
package stencil.parser.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.antlr.runtime.Token;

public class TuplePrototype extends StencilTree implements List<String> {
	public TuplePrototype(Token source) {super(source);}
	
	/**Get the field specified.*/
	public String get(int idx) {return getChild(idx).getText();}
	
	public int size() {return getChildCount();}

	public boolean contains(Object o) {return indexOf(o) >=0;}

	public boolean containsAll(Collection<?> c) {
		for (Object o: c) {if (!contains(o)) {return false;}}
		return true;
	}


	public int indexOf(Object o) {
		if (!(o instanceof String)) {return -1;}
		String alter = (String) o;
		
		int i=0;
		for (String instance : this) {if (alter.equals(instance)) {return i;} i++;}
		return -1;
	}

	public boolean isEmpty() {return getChildCount() == 0;}

	public Iterator<String> iterator() {
		return new Iterator<String> () {
			private int index=0;
			final int size = getChildCount();
			
			public boolean hasNext() {return index < size;}

			public String next() {
				String value = getChild(index).getText();
				index++;
				return value;
			}

			public void remove() {throw new UnsupportedOperationException();}			
		};
	}

	public int lastIndexOf(Object o) {throw new UnsupportedOperationException();}
	
	/**A tuple prototype (at some level) is a list of names.  Get the list here.*/
	public String[] getNames() {return (String[]) toArray();}
	
	public Object[] toArray() {
		String[] o= new String[size()];
		return copyInto(o);
	}
	
	public <T> T[] toArray(T[] a) {
		if (!(a instanceof String[])) {throw new UnsupportedOperationException();}
		
		if (a.length >= size()) {return (T[]) copyInto((String[]) a);}
		
		return (T[]) toArray();
	}
	
	private String[] copyInto(String[] target) {
		int i=0;
		for (String n: this) {target[i++] = n;}
		if (target.length > size()) {target[size()] = null;}
		return target;
	}

	public List<String> subList(int fromIndex, int toIndex) {throw new UnsupportedOperationException();}


	public ListIterator<String> listIterator() {throw new UnsupportedOperationException();}
	public ListIterator<String> listIterator(int index) {throw new UnsupportedOperationException();}
	
	public boolean add(String o) {throw new UnsupportedOperationException();}
	public void add(int index, String element) {throw new UnsupportedOperationException();}
	public boolean addAll(Collection<? extends String> c) {throw new UnsupportedOperationException();}
	public boolean addAll(int index, Collection<? extends String> c) {throw new UnsupportedOperationException();}
	public void clear() {throw new UnsupportedOperationException();}

	public boolean remove(Object o) {throw new UnsupportedOperationException();}
	public String remove(int index) {throw new UnsupportedOperationException();}
	public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
	public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
	public String set(int index, String element) {throw new UnsupportedOperationException();}

}
