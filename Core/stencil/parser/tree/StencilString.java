package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.STRING;

public final class StencilString extends Atom {
	public StencilString(Token token) {super(token, STRING);}

	public String getString() {return getValue();}
	public String toString() {return getValue();}
	public String getValue() {return token.getText();}
}
