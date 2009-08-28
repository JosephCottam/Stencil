package stencil.parser.tree;

import org.antlr.runtime.Token;

public class External extends StencilTree {
	public External(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) parent.getChild(0);}
}
