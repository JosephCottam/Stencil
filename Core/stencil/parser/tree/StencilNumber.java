package stencil.parser.tree;

import org.antlr.runtime.Token;
import static stencil.parser.string.StencilParser.NUMBER;

public final class StencilNumber extends Atom {
	private final Number value;

	public StencilNumber(Token token) throws NumberFormatException {
		super(token, NUMBER);
		value = parseValue(token.getText());
	}

	public final Number getValue() {return value;}

	/**Getting the integer value is a common case (e.g. it is used during tuple de-referencing).  
	 * This method can be used whenever getValue().intValue() is needed.
	 * @return
	 */
	public final int intValue() {return value.intValue();}

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
		
		return value.equals(alter.getValue());
	}
	
	public int hashCode() {return getValue().hashCode();}
}
