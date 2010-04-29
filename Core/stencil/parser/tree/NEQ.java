package stencil.parser.tree;

import org.antlr.runtime.Token;

public class NEQ extends StencilTree implements BooleanOp {
	public NEQ(Token source) {super(source);}

	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {return lhs != rhs;}
}
