package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.modules.stencilUtil.range.RangeDescriptor;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.ValidationException;
import stencil.parser.string.validators.SpecializerValidator;
import stencil.parser.tree.StencilTree;
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
		String[] ranges = new String[] {
				"n .. 1",			//n in first position, not in last
				"10 .. 9",		//Start index after end
				"-10 .. -20",		//Start index after end
				"-1 .. 5",		//Start index after end
		};
		
		for (String test: ranges) {
			SuppressOutput.suppress();
			boolean failed = false;
			try {new RangeDescriptor(test);}
			catch (ValidationException e) {failed =true;}
			finally {SuppressOutput.restore();}		
			assertTrue(String.format("Expected failure parsing %1$s did not occur.", test), failed);
		}
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
			StencilTree spec = ParseStencil.specializerTree(test);
			if (validate) {SpecializerValidator.apply(spec);}
		}
	}
	
	public void testNullValue() throws Exception {
		StencilTree spec = ParseStencil.specializerTree("[value: NULL]");
		StencilTree valueToken = spec.getChild(0).getChild(0);
		assertEquals("Unexpected token type: " + StencilTree.typeName(valueToken.getType()), StencilParser.NULL, valueToken.getType());
	}
	
	private void testFails(String[] tests) { 
				
		for (String test: tests) {
			
			SuppressOutput.suppress();

			boolean failed = false;
			try {
				StencilTree spec = ParseStencil.specializerTree(test);
				SpecializerValidator.apply(spec);
			}
			catch (ProgramParseException e) {failed = true;}
			catch (ValidationException e) {failed =true;}
			finally {SuppressOutput.restore();}
			
			assertTrue(String.format("Expected failure parsing %1$s did not occur.", test), failed);
		}
	}
}
