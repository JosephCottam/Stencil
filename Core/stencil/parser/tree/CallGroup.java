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

import stencil.streams.Tuple;
import stencil.util.Tuples;

import org.antlr.runtime.Token;

import java.util.List;

/** A call group is a group of chains.  It is assumed
 * that all chains will be executed sequentially, then
 * the last chain will use a tuple of the union of
 * all preceding chains as its arguments.
 *
 * Call groups are formed of at least one call chain,
 * any number of splits and a single join.
 *
 * @author jcottam
 *
 */
public class CallGroup extends StencilTree {
	public CallGroup(Token source) {super(source);}

	/**Get call chains that are members of this call group.*/
	public List<CallChain> getChains() {return new stencil.parser.tree.List.WrapperList<CallChain>(this);}

	/**Applies all of the call chains and returns the result of the
	 * last chain.
	 *
	 * The will return null if any chain returns null.
	 *
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public Tuple apply(Tuple source) throws Exception {
		Tuple env = source; //A running environment
		Tuple result =null; //The most result result

		for (CallChain chain:getChains()) {
			env = Tuples.merge(env, result);
			result = chain.apply(env);
			if (result == null) {return null;}
		}

		return result;
	}
}
