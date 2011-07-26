package stencil.parser.tree;

import static stencil.parser.string.StencilParser.CONST;
import static stencil.parser.string.StencilParser.ID;
import static stencil.parser.string.StencilParser.NUMBER;
import static stencil.parser.string.StencilParser.STRING;
import static stencil.parser.string.StencilParser.NULL;

import org.antlr.runtime.Token;

public class Const extends StencilTree {
	private Object value;
	
	public Const(Token source) {super(source);}
	public Const(Token source, Object value) {
		super(source);
		this.value = value;
	}
	
	public String getName() {return this.getText();}
	public Object getValue() {
		if (value != null) {return value;}
		else if (getChildCount() >0) {return getChild(0);}
		return null;
	}
	
	public void setValue(Object value) {
		assert this.value == null : "Attempt to set value on constant with pre-existing value";		
		this.value = value;
	}
	
	
	public Const dupNode() {
		Const n = (Const) super.dupNode();
		n.value = value;
		return n;
	}
	
	public String toString() {
		return getValue() == null ? "null" : getValue().toString();
	}

	public boolean equals(Object other) {
		if (super.equals(other)) {
			if (value == null) {return ((Const) value) == null;}
			else {return value.equals(((Const) other).value);}
		}
		return false;
	}
	
	private static final StencilTreeAdapter adaptor = new StencilTreeAdapter();
	public static StencilTree instance(Object value) {return instance(value, false);}
	public static StencilTree instance(Object value, boolean idBiased) {
		if (value == null) {
			return (StencilTree) adaptor.create(NULL, "NULL");			
		} else if (value instanceof Number) {
			return (StencilTree) adaptor.create(NUMBER, ((Number) value).toString());
		} else if (value instanceof String && idBiased) {
			return (StencilTree) adaptor.create(ID, value.toString());
		} else if (value instanceof String) {
			return (StencilTree) adaptor.create(STRING, value.toString());
		} else {
			Const constant = (Const) adaptor.create(CONST,"CONST");
			constant.setValue(value);
			return constant;
		}
	}
}
