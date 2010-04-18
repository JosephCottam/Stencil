package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.LAST;

public class Last extends Atom {
	private static final Object VALUE = "LAST";

	public Last(Token source) {super(source, LAST);}
	public Last() {super();}

	public Object getValue() {return VALUE;}

	public boolean equals(Object other) {return other instanceof Last;}
	public int hashCode() {return VALUE.hashCode();}
}
