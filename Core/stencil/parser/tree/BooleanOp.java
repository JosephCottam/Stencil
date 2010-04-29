package stencil.parser.tree;

public interface BooleanOp {
	/**Given a left and right hand value, what does this boolean operator evaluate to?*/
	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral);
}
