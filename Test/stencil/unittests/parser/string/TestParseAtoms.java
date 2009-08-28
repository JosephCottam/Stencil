package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.string.StencilParser;

public class TestParseAtoms extends TestCase {
	
	
	/**Test that numbers are parsed when expected, and that non-numbers do not come back as numbers.*/
	public void testNumbers() {
		Number[] numbers = new Number[] {
				0, 1, 20, 31231,
				-1, -24,-39203,
				0.0, 1.1, 1094.19043, 0.1093, .85, .90000, 9.00000
		};
		
		String[] nonNumbers = new String[] {"a", "", "000", "0"};
		
		for (Number n: numbers) {
			String input = n.toString();
			StencilParser parser =  ParserUtils.MakeParser(input);

			StencilParser.atom_return rv = null;
			try {rv = parser.atom();}
			catch (Exception e) {throw new RuntimeException(String.format("Error parsing %1$s.", input), e);}
			
			assertEquals(rv.getTree().toString(), input);
		}

		for (String nn: nonNumbers) {
			boolean failed = false;
			StencilParser parser =  ParserUtils.MakeParser(nn);

			try {parser.atom();}
			catch (Exception e) {failed = true;}
			
			assertFalse("Parser did not fail on atom parse with input " + nn, failed);
		}
	}

}
