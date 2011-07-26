package stencil.unittests.module.operator;

import java.util.Arrays;

import stencil.adapters.java2D.Adapter;
import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.Projection;
import stencil.parser.ParseStencil;
import stencil.parser.ParserConstants;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.OperatorProxy;
import stencil.parser.tree.StencilTree;
import stencil.unittests.StencilTestCase;

public class TestProjection extends StencilTestCase {
	final Module project;
	
	public TestProjection() throws Exception {
		project = new Projection();
	}

	public void testCount() throws Exception {
		StencilOperator op;
		
		String simple = "stream S(A,B,C) layer L from S ID: Count()";		
		StencilTree s = ParseStencil.programTree(simple, Adapter.ADAPTER);
		op = ((OperatorProxy) s.find(StencilParser.LIST_OPERATORS).getChild(0)).getOperator();
		assertEquals("Did not find simple operator when expected.", "Counter", op.getOperatorData().getTarget());
		
		String complex = "stream S(A,B,C) layer L from S ID: Count(A)";
		StencilTree c = ParseStencil.programTree(complex, Adapter.ADAPTER);
		op = ((OperatorProxy) c.find(StencilParser.LIST_OPERATORS).getChild(0)).getOperator();
		assertEquals("Did not find complex operator when expected.", "Count", op.getOperatorData().getTarget());
	}
	
	private void testRankOp(StencilOperator op, int[] ranks, Object[][] inputs) throws Exception {
		Invokeable inv = op.getFacet(ParserConstants.MAP_FACET);
		for (int i=0; i<inputs.length; i++) {
			Object rslt = inv.invoke(inputs[i]);
			assertEquals("Rank not as expected for " + Arrays.deepToString(inputs[i]), ranks[i],rslt);
		}
		
		
	}
	
	public void testRankSingle() throws Exception {
		String single = "stream S(A,B,C) layer L from S ID: Rank(A)";
		StencilTree program = ParseStencil.programTree(single, Adapter.ADAPTER);
		StencilTree operators = program.find(StencilParser.LIST_OPERATORS);
		assertEquals("Unexpected number of operators created.", 1, operators.getChildCount());
		StencilOperator op = ((OperatorProxy) operators.getChild(0)).getOperator();
		
		assertEquals("Unexpected operator found.", "RankSingle", op.getName());
		assertEquals("Unexpected class for single-element rank.", stencil.modules.Projection.RankSingle.class, op.getClass());
		
		testRankOp(op, new int[]{0,1,2,2,1,0,0,3,2}, 
					   new Object[][]{new Object[]{"ape"}, 
									  new Object[]{"cat"},
									  new Object[]{"dog"}, 
							   		  new Object[]{"dog"}, 
							   		  new Object[]{"cat"},
					   				  new Object[]{"ape"}, 
					   				  new Object[]{"ape"}, 
					   				  new Object[]{"emu"}, 
					   				  new Object[]{"dog"}});
	}
	
	public void testRankMultiple() throws Exception {
		String multiple = "stream S(A,B,C) layer L from S ID: Rank(A,B,C)";
		StencilTree program = ParseStencil.programTree(multiple, Adapter.ADAPTER);
		StencilTree operators = program.find(StencilParser.LIST_OPERATORS);
		assertEquals("Unexpected number of operators created.", 1, operators.getChildCount());
		StencilOperator op = ((OperatorProxy) operators.getChild(0)).getOperator();
		
		assertEquals("Unexpected operator found.", "Rank", op.getName());
		assertEquals("Unexpected class for single-element rank.", stencil.modules.Projection.Rank.class, op.getClass());

		testRankOp(op, new int[]{0,1,2,2,1,0,0,3,2}, 
				  new Object[][]{new Object[]{"ape", "ape"}, 
								 new Object[]{"cat", "ape"},
								 new Object[]{"dog", "ape"}, 
								 new Object[]{"dog", "ape"}, 
								 new Object[]{"cat", "ape"},
								 new Object[]{"ape", "ape"}, 
								 new Object[]{"ape", "ape"}, 
								 new Object[]{"dog", "cat"}, 
								 new Object[]{"dog", "ape"}});
	}
}
