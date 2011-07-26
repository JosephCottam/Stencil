 package stencil.unittests.module.operator;

import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.Average;
import stencil.parser.ParseStencil;
import stencil.parser.ParserConstants;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;
import stencil.parser.tree.StencilTree;
import stencil.unittests.StencilTestCase;

public class TestMean extends StencilTestCase {
	final Module average;
	
	public TestMean() throws Exception {
		average = new Average();
	}
	
	
	private static Context makeContext(String name, String highOrderUse, String highOrderSpec) {
		Context c = new Context(name);
		try {
			StencilTree use = (StencilTree) ParseStencil.TREE_ADAPTOR.create(StencilParser.OPERATOR_REFERENCE, highOrderUse);
			StencilTree base = (StencilTree) ParseStencil.TREE_ADAPTOR.create(StencilParser.OPERATOR_BASE, highOrderUse);
			StencilTree spec = ParseStencil.specializerTree(highOrderSpec);
			ParseStencil.TREE_ADAPTOR.addChild(base, spec);
			ParseStencil.TREE_ADAPTOR.addChild(use, base);
			c.addHighOrderUse(highOrderUse, use);
		} catch (Exception e) {throw new Error("Error in test setup",e);}
		return c;
	}
	
	public void testFullRange() throws Exception {
		StencilOperator meaner = average.instance("Mean", makeContext("Mean", "Range", "[range: ALL]"), ParserConstants.EMPTY_SPECIALIZER);
		Invokeable map = meaner.getFacet(StencilOperator.MAP_FACET);
		Invokeable query = meaner.getFacet(StencilOperator.QUERY_FACET);
		
		int count =0;
		double sum =0;
		
		for (int i=0; i < 1000; i++) {
			sum = sum+i;
			count++;
			map.invoke(new Object[]{i});
			assertEquals(sum/count, query.invoke(new Object[0]));			
		}
	}
}
