package stencil.parser.tree;

import org.antlr.runtime.Token;
public class Define extends StencilTree {

	public Define(Token token) {super(token);}
	
	public Id getID() {return (Id) getChild(0);}
	public TuplePrototype getValues() {return (TuplePrototype) getChild(1);}	
}
