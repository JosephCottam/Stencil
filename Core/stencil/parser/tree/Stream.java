package stencil.parser.tree;

import org.antlr.runtime.Token;

public class Stream extends StencilTree {
	public Stream(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}
}
