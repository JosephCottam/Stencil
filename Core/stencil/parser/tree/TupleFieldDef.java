package stencil.parser.tree;

import org.antlr.runtime.Token;

public class TupleFieldDef extends StencilTree implements stencil.tuple.TupleFieldDef {
	public TupleFieldDef(Token token) {super(token);}

	public Class getFieldType() {return null;}
	public String getFieldName() {
		return null;
	}
}
