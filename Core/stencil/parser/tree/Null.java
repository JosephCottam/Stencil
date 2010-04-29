package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.LAST;

public class Null extends Atom {
	public Null(Token source) {super(source, LAST);}
	public Null() {super();}

	public Object getValue() {return null;}
	public boolean isNull() {return true;}

	public boolean equals(Object other) {return other instanceof Null;}
	public int hashCode() {return getClass().hashCode();}
}
