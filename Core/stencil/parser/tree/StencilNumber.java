package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.NUMBER;

public class StencilNumber extends Atom {
	Number value;

	public StencilNumber(Token token) throws NumberFormatException {
		super(token, NUMBER);
		value = parseValue(token.getText());
	}

	public Number getNumber() {return getValue();}
	public Number getValue() {return value;}

	private static final Number parseValue(String value) throws NumberFormatException {
		try {return new Integer(value);}
		catch (NumberFormatException e) {/*Error permitted, and ignored.*/}

		try {return new Double(value);}
		catch (NumberFormatException e) {/*Error permitted, and ignored.*/}

		throw new NumberFormatException("String " + value + " does not encode valid integer or double.");
	}
	
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof StencilNumber)) {return false;}
		StencilNumber alter = (StencilNumber) other;
		
		return this.getNumber().equals(alter.getNumber());
	}
	
	public int hashCode() {return getNumber().hashCode();}
}
