 package stencil.unittests.module.operator;

import java.lang.reflect.Array;

import stencil.unittests.StencilTestCase;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.Summary;
import stencil.parser.ParseStencil;

public class TestMaxAndMin extends StencilTestCase {
	private static final int[] ORDERED_INTS = new int[]{1,2,3,4,5,6,7,8,10};
	private static final int[] MIXED_INTS = new int[]{3,3,5,1,6,-1,3,6,54,1,9};
	private static final Summary SUMMARY = new Summary();
	private static final Specializer SPEC;
	static {
		try {SPEC = ParseStencil.specializer("[c:\"Comparable\"]");}
		catch (Exception e) {throw new Error(e);}
	}
	
	public void testFullMax() {
		StencilOperator op = SUMMARY.instance("FullMax", null, SPEC);
		Invokeable inv = op.getFacet("map");
		runTest(inv, ORDERED_INTS, 10);
		runTest(inv, MIXED_INTS, 54);
	}

	public void testFullMin() {
		StencilOperator op = SUMMARY.instance("FullMin", null, SPEC);
		Invokeable inv = op.getFacet("map");
		runTest(inv, ORDERED_INTS, 1);
		runTest(inv, MIXED_INTS, -1);
	}
	
	public void testMax() {
		StencilOperator op = SUMMARY.instance("Max", null, SPEC);
		Invokeable inv = op.getFacet("map");
		runTest(inv, ORDERED_INTS, 10);
		runTest(inv, MIXED_INTS, 9);
	}
	
	public void testMin() {
		StencilOperator op = SUMMARY.instance("Min", null, SPEC);
		Invokeable inv = op.getFacet("map");
		runTest(inv, ORDERED_INTS, 10);
		runTest(inv, MIXED_INTS, 9);
	}
	
	private void runTest(Invokeable inv, Object values, Object finalResult) {
		Object rv = null;
		int length = Array.getLength(values);
		for (int i=0; i<length; i++) {
			rv = inv.invoke(new Object[]{Array.get(values, i)});
		}
		assertEquals(finalResult, rv);
	}
}
