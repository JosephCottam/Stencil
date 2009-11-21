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

import java.util.List;

import org.antlr.runtime.Token;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

public class Filter extends Target {
	public static final Tuple PASS = PrototypedTuple.singleton(true);
	public static final Tuple FAIL = PrototypedTuple.singleton(false);

	public Filter(Token source) {super(source);}

	public List<Predicate> getPredicates() {
		return (List<Predicate>) getChild(0);
	}

	/**Checks member filter against the source.
	 * The source should be result of calling the rule's apply.
	 *
	 * @returns The special tuple FAIL if any member filer fails,
	 * or the special tuple PASS if all member filter pass.
	 */
	public Tuple finalize(Tuple source) throws Exception {
		for (Predicate pred: getPredicates()) {
			if (!pred.matches(source)) {return FAIL;}
		}
		return PASS;
	}

	/**What is the rule associated with this predicate?*/
	public CallChain rule() {return (CallChain) getChild(1);}

	/**Does the passed tuple match the given predicates?*/
	public boolean matches(Tuple source) throws Exception {
		Tuple result = rule().apply(source);
		return finalize(result) == PASS;
	}
}