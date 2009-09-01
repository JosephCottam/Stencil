package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.ProgramParseException;
import stencil.parser.string.ParseStencil;


public class TestSpecializerParse extends TestCase {
	public void testRangePass() throws Exception {
		String[] specializers = new String[] {
				"[1 .. n]",
				"[n .. n]",
				"[-1 .. n]",
				"[1 .. 2]",
				"[1 .. -10]",
				"[-10 .. -1]"
		};
		testPasses(specializers);
	}
	
	public void testRangeFail() throws Exception {
		String[] specializers = new String[] {
				"[n .. 1]",			//n in first position, not in last
				"[10 .. 9]",		//Start index after end
				"[-10 .. -20]",		//Start index after end
				"[-1 .. 5]",		//Start index after end
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

	
	public void testArgsList() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[\"one\"]",
				"[\"one\", \"two\"]",
				"[\"one\", \"two\", \"three\"]",
				"[\"one\", \"two\", \"three\", \"four\"]",
				"[\"one\", \"two\", \"three\", \"four\", \"five\", \"six\", \"seven\", \"eight\",\"nine\"]",
				"[1]",
				"[1, 2,3]",
				"[1, \"two\", 3, \"four\"]"};
		testPasses(specializers);
	}
	
	public void testRangeArgs() throws Exception {
		String[] specializers = new String[] {
				"[1 .. n, 1]",
				"[1 .. n, \"one\"]",
				"[1 .. n, \"one\", \"two\", \"three\"]",
				"[1 .. n, 1, 2,3]",
				"[1 .. n, 1, \"two\", 3, \"four\"]",
				"[1 .. n, \"one one\", \"two, two\", \" three, three \"]"
				};
		testPasses(specializers);
	}
	
	
	public void testNamedArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[one=1.0]",			
				"[one=1]",			
				"[one=\"one\", two=\"two\"]",
				"[one=\"one\", two=2, three=\"three\"]",
				"[\"one\", two=\"two\"]",		//un-named, followed by named
				"[\"one\", \"two\", three=\"three\", four=\"four\", five=5]"};

		
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
