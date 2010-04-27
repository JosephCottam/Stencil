package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;


public class TestSpecializerParse extends TestCase {
	public void testRangePass() throws Exception {
		String[] specializers = new String[] {
				"[range: \"1..n\"]",
				"[range: \"n .. n\"]",
				"[range: \"-1..n\"]",
				"[range: \"1 .. 2\"]",
				"[range: \"1 .. -10\"]",
				"[range: \"-10 .. -1\"]"
		};
		testPasses(specializers);
	}
	
	public void testRangeFail() throws Exception {
		String[] specializers = new String[] {
				"[range: \"n .. 1\"]",			//n in first position, not in last
				"[range: \"10 .. 9\"]",		//Start index after end
				"[range: \"-10 .. -20\"]",		//Start index after end
				"[range: \"-1 .. 5\"]",		//Start index after end
		};
		testFails(specializers);
	}

	public void testGeneralFail() throws Exception {
		String[] specializers = new String[] {
				"[one, two]",		//No atoms
				"[\"one\", two]"	//Non-atom in second spot
		};
		testFails(specializers);
	}

	
	public void testRangeArgs() throws Exception {
		String[] specializers = new String[] {
				"[range: \"1..n\", more: 1]",
				"[range: \"1..n\", more: \"one\"]",
				"[range: \"1..n\", more: \"one\", more2: \"two\", more3: \"three\"]",
				"[range: \"1..n\", more: 1, more2: 2, more3: 3]",
				"[range: ALL]",
				"[range: LAST]"
				};
		testPasses(specializers);
	}
	
	
	public void testNamedArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[one: 1.0]",			
				"[one: 1]",			
				"[one: \"one\", two: \"two\"]",
				"[one: \"one\", two: 2, three: \"three\"]"};

		
		testPasses(specializers);
	}

	
	private void testPasses(String[] tests) throws Exception {
		for (String test: tests) {
			ParseStencil.parseSpecializer(test);
		}
	}
	
	private void testFails(String[] tests) {
		for (String test: tests) {
			boolean failed = false;
			try {ParseStencil.parseSpecializer(test);}
			catch (ProgramParseException e) {failed = true;}
			assertTrue(String.format("Expected failure parsing %1$s did not occur.", test), failed);
		}
	}
	
	
}
