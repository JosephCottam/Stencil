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
	public static final class Literal extends Atom {
		private enum Type {String, Number, Color}
		Object value;
		Type type;
		
		public static Literal instance(Object o) {
			if (o ==null) {return null;}
			return new Literal(o);
		}

		private Literal(Object o) {
			value = o;
			if (o instanceof java.awt.Color) {type = Type.Color;}
			else if (o instanceof Number) {type = Type.Number;}
			else if (o instanceof String) {type = Type.String;}
			else {
				//TODO: Fix when we have sigil-types
				value = o.toString();
				type = Type.String;
			}
//			else {
//				throw new IllegalArgumentException(String.format("Literal tuple can only contain color, string or number.  Recieved %1$s of type %2$s", o, (o==null?"n/a":o.getClass().getName())));
//			}
		}

		public boolean isLiteral() {return true;}
		public Object getValue() {return value;}
		public String getName() {throw new IllegalArgumentException("Literal atom cannot be a name.");}

		public java.awt.Color getColor() {
			if (isColor()) {return (java.awt.Color) value;}
			throw new IllegalArgumentException(String.format("Literal tuple is of type %1$s, cannot retrieve as color.", type));
		}

		public java.lang.Number getNumber() {
			if (isNumber()) {return (java.lang.Number) value;}
			throw new IllegalArgumentException(String.format("Literal tuple is of type %1$s, cannot retrieve as number.", type));

		}

		public String getString() {
			if (isString()) {return (String) value;}
			throw new IllegalArgumentException(String.format("Literal tuple is of type %1$s, cannot retrieve as string.", type));
		}

		public boolean isName() {return false;}
		public boolean isColor() {return type == Type.Color;}
		public boolean isNumber() {return type == Type.Number;}
		public boolean isString() {return type == Type.String;}

		public String toString() {return value.toString();}
	}

	public static final List<Integer> BASE_TYPES;
	static {
		BASE_TYPES =  Arrays.asList(new Integer[]{ID, STRING, NUMBER, ALL});
	}

	protected Atom() {super(null);}
	protected Atom(Token token) {super(token);}
	protected Atom(Token token, int type) {super(token, type);}
	public abstract Object getValue();

	public boolean isAtom() {return true;}

	public boolean isName() {return getType() == ID;}
	public boolean isString() {return getType() == STRING;}
	public boolean isNumber() {return getType() == NUMBER;}
	public boolean isAll() {return getType() == ALL;}

	/**Atoms ignore the getValue Tuple context.
	 * Calling the single-argument getValue is identical to
	 * calling the zero-argument getValue.*/
	public Object getValue(Tuple source) {return getValue();}

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
}
