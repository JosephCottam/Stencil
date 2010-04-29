package stencil.parser.tree;

import org.antlr.runtime.Token;

public final class EQ extends StencilTree implements BooleanOp {
	public EQ(Token source) {super(source);}
	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {return lhs == rhs;}

}
