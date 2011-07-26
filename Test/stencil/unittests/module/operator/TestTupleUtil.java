package stencil.unittests.module.operator;

import junit.framework.TestCase;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.TupleUtil;
import stencil.parser.ParseStencil;
import stencil.tuple.Tuple;

public class TestTupleUtil extends TestCase {
	TupleUtil tupleUtils;
	
	public void setUp() throws Exception {
		super.setUp();
		tupleUtils = new TupleUtil();
	}

	
	public void testToTuple() throws Exception {
		Specializer spec = ParseStencil.specializer("[CONVERT: \"java.lang.Object\"]");
		StencilOperator op = tupleUtils.instance("ToTuple", null, spec);
		Invokeable f = op.getFacet("query");

		Object r = f.invoke(new Object[]{"one"});
		assertTrue(r instanceof Tuple);
		
		Tuple t =(Tuple) r;
		assertEquals(1, t.size());
		assertEquals("one", t.get(0));
	}
	
}
