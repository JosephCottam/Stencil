package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.tuple.prototype.TupleFieldDef;
public class Define extends StencilTree {

	public Define(Token token) {super(token);}
	
	public Id getID() {return (Id) getChild(0);}
	public TupleFieldDef getValues() {return (TupleFieldDef) getChild(1);}	
}
