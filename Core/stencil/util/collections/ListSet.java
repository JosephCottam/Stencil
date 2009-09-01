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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**A list that does not allow null or duplicates.
 * If an attempt to place a null or duplicate item into the list is made, an IllegalArgumentException is thrown.
 * As a convenience, a 'move' method has been provided to support changing an element's position.
 *
 */
public class ListSet<T> extends ArrayList<T> implements Set<T> {
	private static final long serialVersionUID = 1L;

	public ListSet(T... values) {super(java.util.Arrays.asList(values));}

	public ListSet(Collection<T> source) {
		for (T value:source) {this.add(value);}
	}

	public void  add(int i, T element) {
		validateItem(element);
		super.add(i, element);
	}
	public boolean add(T element) {
		validateItem(element);
		return super.add(element);
	}

	public boolean addAll(Collection<? extends T> c) {
		for (Object g:c) {validateItem(g);}
		return super.addAll(c);
	}
	public T set(int index, T element) {
		validateItem(element);
		return super.set(index, element);
	}

	/**Move the element to the target index.
	 * This method only works if the element is the list, otherwise an IllegalArgumentException is thrown.
	 */
	public void move(int index, T element){
		if (!super.contains(element)) {throw new IllegalArgumentException("Cannot move item not in the list.");}
		super.remove(element);
		super.add(index, element);
	}

	private boolean validateItem(Object candidate) {
		if (candidate == null) {throw new IllegalArgumentException("Null values not permitted.");}
		else if (this.contains(candidate)) {throw new IllegalArgumentException("Duplicate values not permitted.  Use move instead.");}
		return true;
	}
}