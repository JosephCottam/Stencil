 package stencil.unittests.module.operator;

import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.Filters;
import stencil.parser.ParserConstants;
import stencil.unittests.StencilTestCase;

public class TestFilter extends StencilTestCase {
	private static final Object[][] TESTS = 
		new Object[][]{new Object[]{3,3},
   					   new Object[]{3,2},
   					   new Object[]{-3,-4},
   					   new Object[]{3,4},
   					   new Object[]{-4,-3}};

	private final Module filters = new Filters();

	public void NEQ() throws Exception {
		runComparisons("LTE", new boolean[]{false, true, true, true, true});
	}

	public void EQ() throws Exception {
		runComparisons("LTE", new boolean[]{true, false, false, false, false});
	}

	
	public void testLTE() throws Exception {
		runComparisons("LTE", new boolean[]{true, false, false, true, true});
	}

	public void testLT() throws Exception {
		runComparisons("LT", new boolean[]{false, false, false, true, true});
	}
	
	public void testGTE() throws Exception {
		runComparisons("GTE", new boolean[]{true, true, true, false, false});
	}
	
	public void testGT() throws Exception {
		runComparisons("GT", new boolean[]{false, true, true, false, false});
	}

	
	private void runComparisons(String opName, boolean[] expected) throws Exception {
		StencilOperator gte = filters.instance(opName, ParserConstants.EMPTY_SPECIALIZER);
		Invokeable inv = gte.getFacet("map");
		for (int i=0; i< expected.length; i++) {
			assertEquals("Error on comparison " + i, expected[i], inv.invoke(TESTS[i]));
		}
	}

}
