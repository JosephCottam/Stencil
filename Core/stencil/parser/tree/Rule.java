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

import stencil.tuple.Tuple;
import stencil.parser.string.StencilParser;

/** A rule is a group of calls and a tuple prototype.
 * This is a complete, individual mapping definition.
 *
 * @author jcottam
 *
 */
public class Rule extends StencilTree {
	public static final String SOURCE_RESULT = "RULE_RESULT";

	public Rule(Token source) {super(source);}

	/**What is being modified by this rule?*/
	public Target getTarget() {return (Target) getChild(0);}
	
	/**What actions are being taken in this rule*/
	public CallChain getAction() {return (CallChain) getChild(1);}

	/**Is the binding dynamic?*/
	public boolean isDyanmic() {return getChild(2).getType() == StencilParser.DYNAMIC;}
	
	/**Is the binding static?*/
	public boolean isStatic() {return getChild(2).getType() == StencilParser.DEFINE;}

	/**What group does this rule belong to?*/
	public Consumes getGroup() {
		Tree t = this.getAncestor(StencilParser.CONSUMES);
		if (t == null) {throw new RuntimeException("Rules not part of a layer do not belong to a group.");}
		return (Consumes) t;
	}
	
	/**What is the string of named entities (layers, operators, consumes blocks, etc.) to get to this rule.*/
	public String getPath() {
		StringBuilder path = new StringBuilder();
		
		try { 
			//Layer
			Consumes c = getGroup();
			path.append(c.getStream());
			path.append(".");
			path.append(c.getLayer().getName());
		} catch (RuntimeException e) {
			//Operator....
			path.append(this.getParent().getParent().getText());
		}
		return path.toString();
	}
	
	/**Execute the action and return a tuple with fields
	 * corresponding to the prototype and values from the action.
	 *
	 * @param source
	 * @return
	 */
	public Tuple apply(Tuple source) throws Exception {
		Tuple t = getAction().apply(source);
		if (t==null) {return null;}
		else {return getTarget().finalize(t);}
	}
}
