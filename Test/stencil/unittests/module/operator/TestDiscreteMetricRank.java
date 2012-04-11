package stencil.unittests.module.operator;

import stencil.unittests.StencilTestCase;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.Temp;
import stencil.parser.ParseStencil;

public class TestDiscreteMetricRank  extends StencilTestCase {
	private final Temp module = new Temp();
	Specializer spec;
	StencilOperator op;
	
	public void setUp() throws Exception {
		super.setUp();
		spec = ParseStencil.specializer("[margin: 1]");
		op = module.instance("DiscreteMetricRank", spec);
	}
	
	public void testRanking() throws Exception {
		Invokeable map = op.getFacet("map");
		
		long[] args    = new long[]{1,2,6,4,6,3,6,5,6,6,7};
		long[] maps    = new long[]{0,1,2,2,3,2,4,4,5,5,6}; 
		
		assertEquals("Test improperly configured." ,args.length, maps.length);

		for (int i=0; i< args.length; i++) {
			Object r = map.invoke(new Object[]{args[i]});
			assertEquals("Error in map " + i + " (arg " + args[i] + ")", new Long(maps[i]), r);
		}		
	}
	
	
	public void testQuery() throws Exception {
		Invokeable map = op.getFacet("map");
		Invokeable query = op.getFacet("query");
	
		long[] args    = new long[]{1,2,6,4,6,3,6,5,6,6,7};
		
		for (int i=0; i< args.length; i++) {map.invoke(new Object[]{args[i]});}
	
		for (int i=0; i< args.length; i++) {
			Object r = query.invoke(new Object[]{args[i]});
			assertEquals("Error in query " + i + " (arg " + args[i] + ")", new Long(args[i]-1), r);
		}		
	}
	
	public void testMemVisRanks() throws Exception {
		Invokeable map = op.getFacet("map");
				
		long[] args    = new long[]{6319168,6319136 ,46912818536552l,46912818536448l,6318792,46912818536696l,6318920,4197452};
		long[] maps    = new long[]{0,0,2,2,0,5,1,0}; 
		
		assertEquals("Test improperly configured." ,args.length, maps.length);

		for (int i=0; i< args.length; i++) {
			Object r = map.invoke(new Object[]{args[i]});
			assertEquals("Error in map " + i + " (arg " + args[i] + ")", new Long(maps[i]), r);
		}		
	}
	
	
	
}
