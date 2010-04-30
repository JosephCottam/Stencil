package stencil.parser.tree;

import org.antlr.runtime.Token;

public class NEQ extends EQ {
	public NEQ(Token source) {super(source);}

	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {return !super.evaluate(lhs, rhs, rhsLiteral);}
}
