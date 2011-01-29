package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.StencilTree;
import stencil.testUtilities.SuppressOutput;

public class TestParseAtoms extends TestCase {
	
	
	/**Test that numbers are parsed when expected, and that non-numbers do not come back as numbers.*/
	public void testNumbers() {
		Number[] numbers = new Number[] {
				0, 1, 20, 31231,
				-1, -24,-39203,
				0.0, 1.1, 1094.19043, 0.1093, .85, .90000, 9.00000
		};
		
		for (Number n: numbers) {
			String input = n.toString();
			StencilParser parser =  ParserUtils.MakeParser(input);

			StencilParser.atom_return rv = null;
			try {rv = parser.atom();}
			catch (Exception e) {throw new RuntimeException(String.format("Error parsing %1$s.", input), e);}
			
			assertEquals(rv.getTree().toString(), input);
		}
	}
	
	public void testNonNumbers() {
		String[] nonNumbers = new String[] {"a", "1b","1.3.4.5"};
		
		for (String nn: nonNumbers) {
			boolean failed = false;
			StencilParser parser =  ParserUtils.MakeParser(nn);

			SuppressOutput.suppress();
			StencilTree value = null;
			try {value = (StencilTree) parser.atom().getTree();}
			catch (Exception e) {failed = true;}
			finally {SuppressOutput.restore();}
			
			assertTrue(String.format("Parser did not fail on atom parse with input %1$s; returned %2$s.", nn, value), failed);
		}
	}

}
