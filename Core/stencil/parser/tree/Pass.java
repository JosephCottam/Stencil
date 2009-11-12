package stencil.parser.tree;

import org.antlr.runtime.Token;

public abstract class Pass extends StencilTree {
	public Pass(Token token) {super(token);}
	public boolean isYield() {return (this instanceof DirectYield);}
	public abstract String getName();
}
