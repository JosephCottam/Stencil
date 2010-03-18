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

import stencil.operator.StencilOperator;
import stencil.operator.util.Invokeable;
import stencil.tuple.Tuple;


public class Function extends CallTarget {
	private static final class FunctionApplicationException extends RuntimeException {
		public FunctionApplicationException(Function f, Tuple t, Exception e) {
			super(String.format("Error applying function %1$s with tuple %2$s.", f.getName(), t.toString()), e);
		}
	}

	protected Invokeable invokeable;
	protected StencilOperator operator;
		
	public Function(Token source) {super(source);}
	
	public String getName() {return token.getText();}
	public Specializer getSpecializer() {return (Specializer) getChild(0);}
	public List<Value> getArguments() {return (List<Value>) getChild(1);}
	public boolean isTerminal() {return false;}

	public CallTarget getCall() {return (CallTarget) getChild(3);}

	/**
	 * Invokes the current function, and return the result.
	 *
	 * @param valueSource Tuple that supplies non-literal value arguments
	 * @return A tuple that is the result of the end of the call chain
	 * @throws Exception Exceptions may arise during method invocation (either reflection errors, or raised by the method inovked)
	 */
	public Tuple apply(Tuple valueSource) throws Exception {
		try {
			Object[] formals = TupleRef.resolveAll(getArguments(), valueSource);
			Tuple results = (Tuple) getInvokeable().invoke(formals);
			return results;
 		} catch (Exception e) {throw new FunctionApplicationException(this, valueSource, e);} 		
	}

	
	public Invokeable getInvokeable() {return invokeable;}
	public StencilOperator getOperator() {return operator;}

	/**Sets the invokeable for this function.  This will also
	 * set the associate operator since the invokeable comes from
	 * the operator.
	 * 
	 * @param operator
	 * @param facet
	 */
	public void setInvokeable(StencilOperator operator, String facet) {
		this.operator = operator;
		this.invokeable = operator.getFacet(facet);
	}
	
	
	/**Copy operator/invokeable from one function to another.
	 * This method DOES NOT check that the operator is the target for the 
	 * invokeable;  the preferred method to set invokeables is through setInvokeable(StencilOperator, String)
	 * however this method is convenient when manipulating trees.
	 * @param operator
	 * @param invokeable
	 */
	public void setInvokeable(StencilOperator operator, Invokeable invokeable) {
		this.operator = operator;
		this.invokeable = invokeable;
	}
	

	public Function dupNode() {
		Function f = (Function) super.dupNode();
		f.invokeable = invokeable;
		f.operator = operator;
		return f;
	}
	
	/**
	 * Star after the name indicates no invokeable set.
	 * Plus after the name indicates no operator set.
	 */
	public String toString() {
		String s = super.toString();
		if (invokeable == null) {return s + "*";}
		if (operator == null) {return s + "+";}
		return s;
	}
}
