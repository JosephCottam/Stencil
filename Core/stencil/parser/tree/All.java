package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.ALL;


public class All extends Atom {
	private static final Object VALUE = "ALL";

	public All(Token source) {super(source, ALL);}
	public All() {super();}

	public Object getValue() {return VALUE;}

	public boolean equals(Object other) {return other instanceof All;}
	public int hashCode() {return VALUE.hashCode();}
}
