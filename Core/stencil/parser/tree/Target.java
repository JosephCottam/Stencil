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

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;

/**Targets are things that can be on the left-hand-side
 * of a rule.  They consist of a target-type and
 * a tuple prototype.
 *
 * @author jcottam
 *
 */
public final class Target extends StencilTree {
	public Target(Token source) {super(source);}

	/**What is the name of this target (Glyph or Return, etc).*/
	public String getName() {return token.getText();}

	
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}

	/**Create a new tuple where the names are take from the tuple prototype and
	 * values are take from the source.
	 * 
	 * The resulting tuple should is able to update a real target 
	 * (such as a glyph, view or canvas) via merge.
	 */
	public final Tuple finalize(Tuple source) {
		if (source instanceof MapMergeTuple) {
			Tuple[] results = new Tuple[source.size()];
			for (int i=0; i< source.size(); i++) {
				results[i] = finalizeOne(Converter.toTuple(source.get(i)));
			}
			return new MapMergeTuple(results);
		} else {
			return finalizeOne(source);
		}
	}
	
	private final Tuple finalizeOne(Tuple source) {
		String[] fields = TuplePrototypes.getNames(getPrototype());
		Object[] values = new Object[fields.length];

		int size = fields.length;
		for (int i=0; i< size; i++) {values[i] = source.get(i);}

		Tuple rv = new PrototypedTuple(fields, values);
		return rv;
	}
}
