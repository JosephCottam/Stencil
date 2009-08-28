package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.ID;

public final class Id extends Atom {
	protected Id() {super();}
	public Id(Token source) {super(source, ID);}

	public String getValue() {return token.getText();}
	public String getName() {return getValue();}
}
