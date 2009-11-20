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

import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;


/**A legend action is a filter plus a rule.
 *
 * If you think of a simple legend as a switch statement,
 * this is a single case in that switch: Test + body == filter + callBlock
 *
 * */
public class OperatorRule extends StencilTree {
	protected static final int baseType = StencilParser.OPERATOR_RULE;

	public OperatorRule(Token source) {super(source);}

	public List<Predicate> getFilters() {return (List<Predicate>) getChild(0);}
	public List<Rule> getRules() {return (List<Rule>) getChild(1);}

	/**Legend actions 'match' when all of their predicates do.*/
	public boolean matches(Tuple source) {
		boolean passes =true;
		for (Predicate p: getFilters()){
			passes = passes && p.matches(source);
			if (!passes) {break;}
		}
		return passes;
	}

	/**Apply the rules of this legend action to the passed tuple.
	 * This is independent of 'matches', but should only be invoked
	 * if matches passes.
	 *
	 * @param source
	 * @return
	 */
	public Tuple invoke(Tuple source) throws Exception {
		int ruleCount = 0;
		String legendName = getParent().getText();
		Tuple result = null;
		for (Rule rule: getRules()) {
			Tuple buffer;
			try {buffer = rule.apply(source);}
			catch (Exception e) {throw new RuntimeException(String.format("Error invoking sub rule %1$d on operator %2$s.", ruleCount, legendName),e);}

			if (buffer == null) {result = null; break;}
			result= Tuples.merge(result, buffer);
			ruleCount++;
		}

		return result;
	}

}
