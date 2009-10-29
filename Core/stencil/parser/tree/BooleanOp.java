package stencil.parser.tree;

import java.util.regex.Pattern;

import stencil.parser.ParserConstants;
import stencil.types.Converter;

import org.antlr.runtime.Token;

public final class BooleanOp extends StencilTree {
	protected static final java.util.List NUMERIC_OPS;

	Pattern patternCache;
	
	static {
		NUMERIC_OPS = java.util.Arrays.asList(new String[]{
				ParserConstants.GT, ParserConstants.GTE, ParserConstants.LT,
				ParserConstants.LTE, ParserConstants.EQ, ParserConstants.NOT_EQ});
	}

	public BooleanOp(Token source) {super(source);}

	/**What is the operator's string representation?*/
	public String toString() {return token.getText();}

	/**Given a left and right hand value, what does this evaluate to?
	 *
	 * Due to a quirk in the treatments of type in the Stencil system,
	 * you can ask 3 <"4" and it will return TRUE.  Conversion happens
	 * as late as possible, and an attempt is made to parse strings as
	 * numbers.  There is currently no way to remove this behavior.
	 * */
	public boolean evaluate(Atom lhs, Atom rhs) {
		if(token.getText().equals(ParserConstants.REGULAR_EXPRESSION) ||
			token.getText().equals(ParserConstants.NEGATED_REGULAR_EXPRESSION)) {
			assert rhs != null : "Cannot pass a null rhs/pattern to pattern matching operator.";

			boolean normal = token.getText().equals(ParserConstants.REGULAR_EXPRESSION);
			boolean matches = false;

			if (rhs instanceof All) {return true == normal;}
			assert rhs.isString(): "Fills must be either a string or ALL";


			String pattern = (String) rhs.getValue();
			String value = (lhs == null ? null : lhs.getValue().toString());

			if (value == null) {matches = pattern.equals(ParserConstants.NULL_PATTERN);}
			else if (rhs instanceof Atom.Literal) {
				if (patternCache == null) {patternCache = Pattern.compile(pattern);} //Cache a copy of the compiled pattern (for literal patterns)
				matches = patternCache.matcher(value).matches();
			} else {matches = Pattern.matches(pattern, value);}
			
			return normal==matches;
		} else if (NUMERIC_OPS.contains(token.getText())) {
			double l,r;

			//Must do conversion here, since the tuples do not store types, just values.
			try {l = Converter.toDouble(lhs.getValue());}
			catch (NumberFormatException e) {throw new NumberFormatException("Error parsing LHS of implicit numeric filter " + token.getText() + " with value " + lhs);}

			//Must do conversion here, since the tuples do not store types, just values.
			try {r = Converter.toDouble(rhs.getValue());}
			catch (NumberFormatException e) {throw new NumberFormatException("Error parsing RHS of implicit numeric filter " + token.getText() + " with value " + rhs);}


			if (token.getText().equals(ParserConstants.GT)) {return l >  r;}
			else if (token.getText().equals(ParserConstants.GTE)) { return l >= r;}
			else if (token.getText().equals(ParserConstants.LT)) {return l <  r;}
			else if (token.getText().equals(ParserConstants.LTE)) { return l <= r;}
			else if (token.getText().equals(ParserConstants.EQ)) {return l == r;}
			else if (token.getText().equals(ParserConstants.NOT_EQ)) {return l != r;}
		}
		throw new RuntimeException(String.format("Parser accepted operator %1$s which interpreter doesn't understand.", token.getText()));
	}
	}
