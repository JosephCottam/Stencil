package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

public class Const extends StencilTree {
	private Object value;
	private Tuple tuple;
	
	public Const(Token source) {super(source);}
	
	public Object getValue() {return value;}
	public Tuple getTuple() {return tuple;}

	public void setValue(Object value) {
		assert tuple == null : "Attempt to set both tuple and value of constant.";
		this.value = value;
	}

	public void setTuple(Tuple tuple) {
		assert value == null : "Attempt to set both tuple and value of constant.";
		this.tuple = tuple;
	}
}
