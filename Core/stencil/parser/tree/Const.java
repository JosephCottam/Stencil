package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

public class Const extends StencilTree {
	private Tuple tuple;
	
	public Const(Token source) {super(source);}
	
	public String getName() {return this.getText();}
	public Atom getValue() {return (Atom) getChild(0);}
	public Tuple getTuple() {return tuple;}

	public void setTuple(Tuple tuple) {
		assert getChildCount() == 0 : "Attempt to set both tuple and value of constant.";
		this.tuple = tuple;
	}
}
