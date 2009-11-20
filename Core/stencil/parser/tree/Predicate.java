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
import org.antlr.runtime.tree.Tree;

import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;


public class Predicate extends StencilTree {
	public Predicate(Token source) {super(source);}

	public Atom getLHSValue(Tuple tuple) {return getValue(getChild(0), tuple);}

	public Atom getRHSValue(Tuple tuple) {return getValue(getChild(2), tuple);}

	public BooleanOp getOperator() {return (BooleanOp) getChild(1);}

	public boolean matches(Tuple source) {
		if (getChild(0).getType() == StencilParser.ALL) {return true;}

		return getOperator().evaluate(getLHSValue(source), getRHSValue(source));
	}

	/**Gets the value of the source AST.  The source AST may represent a number, quote string or name.
	 * If it is a number, it is returned as a Double.  If it is a quoted string, the string text is
	 * returned.  If it is a name, the value from the tuple with the corresponding name is returned.
	 * If the tuple is null, but a name is passed, null is returned.
	 *
	 * @param source
	 * @param tuple
	 * @return
	 */
	private Atom getValue(Tree source, Tuple tuple) {
		if (source instanceof Value) {
			return Atom.Literal.instance(TupleRef.resolve((Value) source, tuple));
		} else if (source instanceof CallChain){
			Tuple result;
			try {result =  ((CallChain) source).apply(tuple);}
			catch (Exception e) {throw new RuntimeException("Error applying function in predicate.");}

			if (result == null){return null;}
			String firstField = result.getPrototype().get(0);
			return Atom.Literal.instance(result.get(firstField));
		} else {
			throw new RuntimeException("Unrecognized tree trying to extract values for filter.");
		}
	}


}
