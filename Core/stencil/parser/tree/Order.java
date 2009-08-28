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

import java.util.Arrays;
import java.util.List;
import java.lang.Iterable;
import java.util.Iterator;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class Order extends StencilTree implements Iterable<List<? extends Atom>> {
	public Order(Token source) {super(source);}

	/**Get the groups of this ordering.*/
	public Iterator<List<? extends Atom>> iterator() {
		return new Iterator<List<? extends Atom>>() {
			int idx =0;
			public boolean hasNext() {return idx < getChildCount();}

			public List<? extends Atom> next() {
				List<? extends Atom> group = getGroup(idx);
				idx++;
				return group;
			}

			public void remove() {throw new UnsupportedOperationException();}
		};
	}

	/**What items are in the n-th group?*/
	public List<? extends Atom> getGroup(int idx) {
		Tree tree = getChild(idx);

		Id[] fields = new Id[tree.getChildCount()];
		for (int i=0; i< fields.length; i++) {
			fields[i] = (Id) tree.getChild(i);
		}
		return Arrays.asList(fields);
	}
}
