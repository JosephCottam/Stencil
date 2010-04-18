package stencil.parser.tree;

import org.antlr.runtime.Token;

public class ExternalStream extends StencilTree {
	public ExternalStream(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}
}
