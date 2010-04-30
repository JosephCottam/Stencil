package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.types.Converter;

public class EQ extends StencilTree implements BooleanOp {
	public EQ(Token source) {super(source);}
	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {
		if (rhs == null || lhs == null) {return lhs == rhs;}
		if (rhs instanceof Number || lhs instanceof Number) {
			double l = Converter.toDouble(lhs).doubleValue();
			double r = Converter.toDouble(rhs).doubleValue();
			return l == r;
		} else {
			return lhs.equals(rhs);
		}
	}

}
