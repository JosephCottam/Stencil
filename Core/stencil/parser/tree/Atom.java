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

import java.util.Arrays;
import java.util.List;
import org.antlr.runtime.Token;

import stencil.tuple.Tuple;
import static stencil.parser.string.StencilParser.*;

public abstract class Atom extends Value {
	public static final List<Integer> BASE_TYPES;
	static {
		BASE_TYPES =  Arrays.asList(new Integer[]{ID, STRING, NUMBER, ALL});
	}

	private static final StencilTreeAdapter adaptor = new StencilTreeAdapter();

	
	protected Atom() {super(null);}
	protected Atom(Token token) {super(token);}
	protected Atom(Token token, int type) {super(token, type);}
	public abstract Object getValue();

	public boolean isAtom() {return true;}

	public boolean isName() {return getType() == ID;}
	public boolean isString() {return getType() == STRING;}
	public boolean isNumber() {return getType() == NUMBER;}
	public boolean isAll() {return getType() == ALL;}
	public boolean isLast() {return getType() == LAST;}
	public boolean isNull() {return getType() == NULL;}
	public boolean isConst() {return getType() == CONST;}
	
	/**Atoms ignore the getValue Tuple context.
	 * Calling the single-argument getValue is identical to
	 * calling the zero-argument getValue.*/
	public Object getValue(Tuple source) {return getValue();}

	public int hashCode() {return getValue().hashCode();}
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (this.getClass() != other.getClass()) {return false;}
		
		Atom a = (Atom) other;
		return getValue().equals(a.getValue());
	}
	
	public static boolean isAtom(Token token) {return BASE_TYPES.contains(token);}

	public static Atom instance(Token token) {
		switch (token.getType()) {
		case STRING :  return new StencilString(token);
		case NUMBER: return new StencilNumber(token);
		case ALL: return new All(token);
		case ID : return new Id(token);
		}
		throw new IllegalArgumentException("Cannot make atom from tree of type " + StencilTree.typeName(token.getType()));
	}
		
	public static Atom instance(Object value) {return instance(value, false);}
	public static Atom instance(Object value, boolean idBiased) {
		if (value instanceof Number) {
			return (Atom) adaptor.create(NUMBER, ((Number) value).toString());
		} else if (value instanceof String && idBiased) {
			return (Id) adaptor.create(ID, value.toString());
		} else if (value instanceof String) {
			return (Atom) adaptor.create(STRING, value.toString());
		} else {
			Const constant = (Const) adaptor.create(CONST,"CONST");
			constant.setValue(value);
			return constant;
		}
	}
}
