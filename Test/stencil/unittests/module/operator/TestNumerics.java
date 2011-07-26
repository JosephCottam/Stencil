package stencil.unittests.module.operator;

import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.modules.Numerics;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Specializer;
import stencil.unittests.StencilTestCase;

public class TestNumerics extends StencilTestCase {
	final Module numerics;
	
	public TestNumerics() throws Exception {
		numerics = new Numerics();
	}

	public void testLog() throws Exception {
		Specializer spec10 = ParseStencil.specializer("[base: 10, range: LAST]");
		Specializer specE = ParseStencil.specializer("[base: \"e\", range: LAST]");
		Specializer spec2 = ParseStencil.specializer("[base: 2, range: LAST]");
		Specializer specNone = ParseStencil.specializer("[base: NULL, range: LAST]");
		
		StencilOperator log10 = numerics.instance("Log", null, spec10);
		StencilOperator logE = numerics.instance("Log", null, specE);
		StencilOperator log2 = numerics.instance("Log", null, spec2);
		StencilOperator logNone = numerics.instance("Log", null, specNone);
		
		assertEquals(InvokeableOperator.class, log10.getClass());
		assertEquals(InvokeableOperator.class, logE.getClass());
		assertEquals(stencil.modules.Numerics.LogFixed.class, log2.getClass());
		assertEquals(InvokeableOperator.class, logNone.getClass());
		
		testInvokes(log10, new Object[]{10, 100, 200, 5}, new Object[]{1.0d, 2.0d, 2.3010299956639813, 0.6989700043360189}); 
		testInvokes(logE, new Object[]{Math.E, 100, 200, 5}, new Object[]{1.0d,  4.605170185988092, 5.298317366548036,  1.6094379124341003}); 
		testInvokes(log2, new Object[]{2, 4, 8, 100, 200, 5}, new Object[]{1.0d, 2.0d, 3.0d, 6.643856189774725, 7.643856189774724, 2.321928094887362});
		testInvokes(logNone, new Object[]{new Object[]{2,2}, new Object[]{3,3},new Object[]{11,11},new Object[]{10.5,10.5}}, 
								new Object[]{1.0d,1.0d,1.0d,1.0d});
	}	
	
	private void testInvokes(StencilOperator op, Object[] input, Object[] expected) {
		assert input.length == expected.length;
		
		Invokeable query = op.getFacet("query");
		for (int i=0; i< input.length; i++) {
			Object[] args;
			if (input[i].getClass().isArray()) {
				args = (Object[]) input[i];
			} else {
				args = new Object[]{input[i]};
			}
			Object result = query.invoke(args);
			assertEquals(op.getName() + " did not return expected result for " + input[i] + ";", expected[i], result);
		}
	}
}
