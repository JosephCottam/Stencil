package stencil.parser.tree;

import java.util.regex.Pattern;

import stencil.types.Converter;

import org.antlr.runtime.Token;

public class RE extends StencilTree implements BooleanOp {
	
	private Pattern patternCache; 
	
	public RE(Token source) {super(source);}

	/**What is the operator's string representation?*/
	public String toString() {return token.getText();}

	/**Given a left and right hand value, what does this evaluate to?
	 *
	 * Due to a quirk in the treatments of type in the Stencil system,
	 * you can ask 3 <"4" and it will return TRUE.  Conversion happens
	 * as late as possible, and an attempt is made to parse strings as
	 * numbers.  There is currently no way to remove this behavior.
	 * */
	public boolean evaluate(Object lhs, Object rhs, boolean rhsLiteral) {
		if (rhs instanceof All) {return true;}

		String pattern = Converter.toString(rhs);
		String value = Converter.toString(lhs);

		
			if (rhsLiteral) {if (patternCache == null) {patternCache = Pattern.compile(pattern);}}
			else {patternCache = Pattern.compile(pattern);}

		return patternCache.matcher(value).matches();
	}
}
