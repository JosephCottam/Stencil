package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;
import static stencil.parser.ParserConstants.BIND_OPERATOR;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;

public class Selector extends StencilTree {
	public Selector(Token token) {super(token);}

	/**What is this selector associated with?*/
	public String getAttribute() {return getChild(0).getText();}

	/**What is being selected?  This is a path statement.*/
	public List<Id> getPath() {return (List) this.getFirstChildWithType(StencilParser.LIST);}


	/**Get a string representation of the path*/
	public String pathString() {
		StringBuilder b = new StringBuilder();
		for (Object o: getPath()) {
			b.append(o.toString());
			b.append(NAME_SEPARATOR);
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append(getAttribute());
		b.append(BIND_OPERATOR);
		b.append(pathString());
		return b.toString();
	}

	public int hashCode() {return toString().hashCode();}
	
	public boolean equals(Object o) {
		if (!(o instanceof Selector)) {return false;}
	    return o.toString().equals(this.toString());
	}
}
