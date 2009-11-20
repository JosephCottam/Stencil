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

import stencil.operator.DynamicStencilOperator;
import stencil.operator.StencilOperator;
import stencil.operator.module.util.Modules;
import stencil.operator.util.Invokeable;
import stencil.tuple.Tuple;
import stencil.util.MultiPartName;


public class Function extends CallTarget {
	private static final class FunctionApplicationException extends RuntimeException {
		public FunctionApplicationException(Function f, Exception e) {
			super("Error applying function " + f.getName(), e);
		}
	}

	protected StencilOperator operator;
	protected Invokeable invokeable;
		
	public Function(Token source) {super(source);}
	
	public String getName() {return token.getText();}
	public Specializer getSpecializer() {return (Specializer) getChild(0);}
	public List<Value> getArguments() {return (List<Value>) getChild(1);}
	public Pass getPass() {return (Pass) getChild(2);}
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
			Tuple results = getInvokeable().invoke(formals);
			return results;
 		} catch (Exception e) {throw new FunctionApplicationException(this, e);} 		
	}

	
	public void setOperator(StencilOperator operator)  {this.operator = operator;}
	
	public StencilOperator getOperator() {return operator;}

	/**What actually needs to be invoked when a call is to be made?*/
	private Invokeable<StencilOperator, Tuple> getInvokeable() throws NoSuchMethodException {
		if (invokeable != null) {return invokeable;}
		
		MultiPartName name = new MultiPartName(getName());
		
		if (operator instanceof DynamicStencilOperator) {
			invokeable = ((DynamicStencilOperator) operator).getFacet(name.getFacet());
		} else {
			invokeable = new Invokeable(Modules.javaCase(name.getFacet()), operator);
		}
		return invokeable;
	}
	
	public Function dupNode() {
		Function f = (Function) super.dupNode();
		f.setOperator(operator);
		return f;
	}
	
	public String toString() {
		String s = super.toString();
		if (operator == null) {return s + "*";}
		return s;
	}
}
