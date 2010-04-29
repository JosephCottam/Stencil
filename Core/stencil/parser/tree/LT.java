package stencil.parser.tree;

import stencil.types.Converter;

import org.antlr.runtime.Token;

public class LT extends StencilTree implements BooleanOp {
	public LT(Token source) {super(source);}
	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {
		double l = Converter.toDouble(lhs);
		double r = Converter.toDouble(rhs);
		return l < r;
	}
}
