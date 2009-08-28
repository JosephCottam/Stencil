package stencil.parser.tree;

import org.antlr.runtime.Token;

public class Yields extends StencilTree {
	public Yields(Token source) {super(source);}
	
	public TuplePrototype getInput() {return (TuplePrototype) getChild(0);}
	public TuplePrototype getOutput() {return (TuplePrototype) getChild(1);}
}
