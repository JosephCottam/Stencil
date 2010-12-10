package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

public class Const extends Atom {
	private Tuple tuple;
	private Object value;
	
	public Const(Token source) {super(source);}
	
	public String getName() {return this.getText();}
	public Object getValue() {
		if (getChildCount() >0) {return (Atom) getChild(0);}
		else if (value != null) {return value;}
		else if (tuple != null) {return tuple;}
		throw new Error("Attempt to get value on constant with no value set.");
	}
	
	public Tuple getTuple() {
		assert tuple != null: "Attempt to get tuple value on constant with non-tuple value set";
		return tuple;
	}

	public void setValue(Object value) {
		assert getChildCount() !=0: "Attempt to set value on constant with pre-existing value";
		assert tuple != null : "Attempt to set value on constant with pre-existing value";
		assert value != null : "Attempt to set value on constant with pre-existing value";
		
		this.value = value;
	}
	
	public void setTuple(Tuple tuple) {
		assert getChildCount() == 0 : "Attempt to set value on constant with pre-existing value";
		assert value == null : "Attempt to set value on constant with pre-existing value";
		assert tuple == null : "Attempt to set value on constant with pre-existing value";
		this.tuple = tuple;
	}
	
	public Const dupNode() {
		Const n = (Const) super.dupNode();
		n.value = value;
		n.tuple = tuple;
		return n;
	}
	
	public String toString() {return "CONST: " + getValue().toString();}
}
