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
import java.util.ListIterator;

import org.antlr.runtime.Token;

import stencil.tuple.TupleFieldDef;

public class TuplePrototype extends StencilTree implements stencil.tuple.TuplePrototype {
	private final java.util.List base;
	
	public TuplePrototype(Token source) {
		super(source);
		base = new List.WrapperList<stencil.parser.tree.TupleFieldDef>(this);
	}

	public TupleFieldDef get(int index) {return (TupleFieldDef) base.get(index);}
	public int indexOf(Object o) {return base.indexOf(o);}
	public boolean isEmpty() {return base.isEmpty();}
	public int size() {return base.size();}

	public Iterator<TupleFieldDef> iterator() {return base.iterator();}
	public int lastIndexOf(Object o) {return base.lastIndexOf(o);}
	public ListIterator<TupleFieldDef> listIterator() {return base.listIterator();}
	public ListIterator<TupleFieldDef> listIterator(int index) {return base.listIterator(index);}
	public java.util.List<TupleFieldDef> subList(int fromIndex, int toIndex) {return base.subList(fromIndex, toIndex);}
	public Object[] toArray() {return base.toArray();}
	public <T> T[] toArray(T[] a) {return (T[]) base.toArray(a);}

	public boolean add(TupleFieldDef e) {throw new UnsupportedOperationException();}
	public void add(int index, TupleFieldDef element) {throw new UnsupportedOperationException();}
	public boolean addAll(Collection<? extends TupleFieldDef> c) {throw new UnsupportedOperationException();}
	public boolean addAll(int index, Collection<? extends TupleFieldDef> c) {throw new UnsupportedOperationException();}
	public void clear() {throw new UnsupportedOperationException();}
	public boolean contains(Object o) {return base.contains(o);}
	public boolean containsAll(Collection<?> c) {return base.containsAll(c);}
	public boolean remove(Object o) {throw new UnsupportedOperationException();}
	public TupleFieldDef remove(int index) {throw new UnsupportedOperationException();}
	public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
	public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
	public TupleFieldDef set(int index, TupleFieldDef element) {throw new UnsupportedOperationException();}

}
