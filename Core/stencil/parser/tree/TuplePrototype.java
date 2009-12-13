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

import java.util.Iterator;
import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;

public class TuplePrototype extends StencilTree implements stencil.tuple.prototype.TuplePrototype<TupleFieldDef> {
	private final java.util.List<TupleFieldDef> base;
	
	public TuplePrototype(Token source) {
		super(source);

		assert verifyType(this, StencilParser.TUPLE_PROTOTYPE);
		base = new List.WrapperList<TupleFieldDef>(this);
	}
	
	public int size() {return base.size();}
	public Iterator<stencil.parser.tree.TupleFieldDef> iterator() {return base.iterator();}
	public boolean contains(String name) {return indexOf(name) >=0;}
	public TupleFieldDef get(int idx) {return base.get(idx);}

	public int indexOf(String name) {
		int idx=0;
		for (TupleFieldDef field: this) {
			if (name.equals(field.getFieldName())) {return idx;}
			idx++;
		}
		return -1;
	}

}
