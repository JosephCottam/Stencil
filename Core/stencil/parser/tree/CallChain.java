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

import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;

/**A call chain is a linear group of calls, ending in a pack.
 * Call chains MAY start with any call target.
 *
 * @author jcottam
 *
 */
public class CallChain extends StencilTree {
	public CallChain(Token source) {super(source);}

	public CallTarget getStart() {return (CallTarget) getChild(0);}

	
	
	/**Execute the call chain, all the way through the pack.
	 * 	 *
	 * Short-circuiting occurs when the method invoked returns null.  If this is the case,
	 * a null is immediately returned from the function chain.  This means that no further
	 * actions will be taken in the chain.  If a 'null' is a valid return value from a given
	 * function, then you must wrap it in a tuple and give it an appropriate key.
	 *
	 * TODO: Add better return logic when null is returned from the call.
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public Tuple apply(Tuple source) throws Exception {
		if (source instanceof Environment) {return apply((Environment) source);}
		else {return apply(new Environment(source));}
	}
	
	public Tuple apply(Environment source) throws Exception {		
		CallTarget target = getStart();

		Tuple result = source;
		while (target instanceof Function) {
			Function func = (Function) target;
			result = func.apply(source);
			if (result == null) {return null;}
			source = source.append(func.getPass().getName(), result);
			target = ((Function) target).getCall();
		}

		assert (target instanceof Pack) : "Call chain ending includes non-pack, non-function: " + target.getClass().getName();
		assert (result != null) : "Call chain ended with null result.";
		
		result = target.apply(source);
		return result;
	}

}
