package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;

public class TupleFieldDef extends StencilTree implements stencil.tuple.prototype.TupleFieldDef {
	public TupleFieldDef(Token token) {super(token);}

	public Class getFieldType() {
		if (getChild(1).getType() == StencilParser.DEFAULT) {
			return Object.class;
		} else {
			throw new Error("Types not properly handled!");
		}
	}
	public String getFieldName() {return getChild(0).getText();}
}
