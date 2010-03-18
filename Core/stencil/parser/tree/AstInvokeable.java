package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.operator.util.Invokeable;

/**Holder for invokeables, independent of context.
 * 
 * Text is usually related to the invokeable, but is not
 * necessarily a consistent relationship across contexts.
 * */
public final class AstInvokeable extends StencilTree {
	private Invokeable inv;
	
	public AstInvokeable(Token token) {
		super(token);
	}
	
	public void setInvokeable(Invokeable inv) {this.inv = inv;}
	public Invokeable getInvokeable() {return inv;}

	public AstInvokeable dupNode() {
		AstInvokeable n = (AstInvokeable) super.dupNode();
		n.inv = inv;
		return n;
	}
	
	public String toString() {
		String rv = super.toString();
		if (inv == null) {return rv + " -NoInvokeable";}
		return rv;
	}
}
