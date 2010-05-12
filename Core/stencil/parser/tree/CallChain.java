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

import stencil.parser.string.StencilParser;
import stencil.parser.tree.util.Environment;
import stencil.tuple.MapMergeTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

/**A call chain is a linear group of calls, ending in a pack.
 * Call chains MAY start with any call target.
 *
 * @author jcottam
 *
 */
public class CallChain extends StencilTree {
	private CallTarget chainStart;
	private int depth = Integer.MIN_VALUE;
	
	public CallChain(Token source) {super(source);}

	public CallTarget getStart() {
		if (chainStart == null) {
			chainStart = (Function) getFirstChildWithType(StencilParser.FUNCTION);
			if (chainStart == null) {
				chainStart = (Pack) getFirstChildWithType(StencilParser.PACK);
			}
		}
		return chainStart;
	}
	
	/**How long is this call chain?*/
	public int getDepth() {
		if (depth <0) {
			depth = ((StencilNumber) getFirstChildWithType(StencilParser.NUMBER)).getNumber().intValue();
		}
		return depth;
	}	
	
	/**Execute the call chain, all the way through the pack.
	 * 	 *
	 * Short-circuiting occurs when the method invoked returns null.  If this is the case,
	 * a null is immediately returned from the function chain.  This means that no further
	 * actions will be taken in the chain.  If a 'null' is a valid return value from a given
	 * function, then you must wrap it in a tuple and give it an appropriate key.
	 *
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public Tuple apply(Environment rootEnv) throws Exception {		
		Environment[] envs = new Environment[]{rootEnv};
		CallTarget target = getStart();
		
		while (!(target instanceof Pack)) {
			final Function func = (Function) target;
			Tuple[] results = new Tuple[envs.length];
			for (int i=0; i< results.length; i++) {
				results[i] = func.apply(envs[i]);
			}
			
			//Depending on the pass operator, there may be 1 or many results
			switch (func.getPass().getType()) {
				case StencilParser.DIRECT_YIELD :
					for (int i=0; i<results.length; i++) {
						envs[i].extend(results[i]);
					}
					break;
				case StencilParser.MAP :
					Environment[] newEnvs = new Environment[results[0].size()];  //HACK: The 0 here is because only one map is allowed to be used at a time...nesting them will require this to be in a loop AT LEAST
					for (int i=0; i< newEnvs.length; i++) {
						newEnvs[i] = envs[0].clone();								//make new env
						newEnvs[i].extend(Converter.toTuple(results[0].get(i)));	//Add part of the most recent result to new env
					}
					envs = newEnvs;
					break;
				case StencilParser.FOLD :
					Tuple folded = new MapMergeTuple(results);
					envs = new Environment[]{envs[0]};
					envs[0].extend(folded);
					break;
			}
			target = ((Function) target).getCall();
		}

		Tuple[] results = new Tuple[envs.length];
		for (int i=0; i< results.length; i++) {
			results[i] = target.apply(envs[i]);	//Target will be pack here
		}
		
		if (results.length >1) {return new MapMergeTuple(results);}
		else if (results.length ==1) {return results[0];}
		else {return null;}
	}

}
