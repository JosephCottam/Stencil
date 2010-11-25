package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.parser.string.ValidationException;
import stencil.parser.string.validators.SpecializerValidator;
import stencil.parser.tree.Specializer;
import stencil.testUtilities.SuppressOutput;


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
		testPasses(specializers, true);
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
		testPasses(specializers,true);
	}
	
	public void testPositionArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[1.0]",			
				"[1]",			
				"[\"one\", \"two\"]",
				"[\"one\", 2, \"three\"]"};
		testPasses(specializers, true);
	}
	
	public void testNamedArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[one: 1.0]",			
				"[one: 1]",			
				"[one: \"one\", two: \"two\"]",
				"[one: \"one\", two: 2, three: \"three\"]"};

		
		testPasses(specializers, true);
	}
	
	public void testMixedArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[1, one: 1.0]",			
				"[1.0, one: 1]",			
				"[\"one\", two: \"two\"]",
				"[\"one\", 2, three: \"three\", four: 4]"};
		testPasses(specializers, true);
	}
	


	//ID arguments can refer to constants.  Right now, just see if they can be parsed.
	public void testIDArgs() throws Exception {
		String[] specializers = new String[] {
				"[]",
				"[one: ONE]",			
				"[one: 1, two: TWO]",
				"[one: \"one\", two: 2, three: THREE]",
				"[one: \"one\", two: TWO, three: 3]"};

		
		testPasses(specializers, false);	//Cannot validate because of constant refs can't be resolved out
	}

	
	private void testPasses(String[] tests, boolean validate) throws Exception {
		for (String test: tests) {
			Specializer spec = ParseStencil.parseSpecializer(test);
			if (validate) {SpecializerValidator.apply(spec);}
		}
	}
	
	private void testFails(String[] tests) { 
				
		for (String test: tests) {
			
			SuppressOutput.suppress();

			boolean failed = false;
			try {
				Specializer spec = ParseStencil.parseSpecializer(test);
				SpecializerValidator.apply(spec);
			}
			catch (ProgramParseException e) {failed = true;}
			catch (ValidationException e) {failed =true;}
			finally {SuppressOutput.restore();}
			
			assertTrue(String.format("Expected failure parsing %1$s did not occur.", test), failed);
		}
	}
}
