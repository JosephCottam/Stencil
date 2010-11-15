package stencil.unittests.parser.string;

import junit.framework.TestCase;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.testUtilities.SuppressOutput;
import stencil.tuple.prototype.TuplePrototype;


public class TestPrototypeParse extends TestCase {
	public void testUntyped() throws Exception {
		String[] prototypes = new String[] {
				"(One)",
				"(OneField)",
				"(One, Two)",
				"(One, Two, Three, four, five, Six, Seven, eight, NINE)",
				"(_One)",
		};
		testPasses(prototypes);		
	}

	public void testFails() throws Exception {
		String[] prototypes = new String[] {
				"(1)",
				"(One, 2)",
				"(2Them)",
				""
		};
		testFails(prototypes);		
	}

	
	public void testTyped() throws Exception {
		String[] prototypes = new String[] {
				"(double OneField)",
				"(int One, int Two)",
				"(double One, tuple Two, int Three, double four, int five, tuple Six, tuple Seven, tuple eight, String NINE)",
				"(int _One)",
		};
		testPasses(prototypes);				
	}

	
	
	private void testPasses(String[] tests) throws Exception {
		for (String test: tests) {
			TuplePrototype p = ParseStencil.parsePrototype(test, true);
			int expected = test.replaceAll("[^,]", "").length() +1;
			assertEquals("Prototype length not as expected: " + test + ".", expected, p.size());
		}
	}
	
	private void testFails(String[] tests) {
		for (String test: tests) {
			boolean failed = false;
			
			SuppressOutput.suppress();
			try {ParseStencil.parsePrototype(test, false);}
			catch (ProgramParseException e) {failed = true;}
			finally{SuppressOutput.restore();}
			
			assertTrue(String.format("Expected failure parsing '%1$s' did not occur.", test), failed);
		}
	}
	
	
}
