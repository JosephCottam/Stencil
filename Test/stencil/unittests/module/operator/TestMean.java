 package stencil.unittests.module.operator;

import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.FacetData;
import stencil.modules.Average;
import stencil.modules.Average.FullMean;
import stencil.parser.ParseStencil;
import stencil.parser.ParserConstants;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;
import stencil.parser.tree.OperatorProxy;
import stencil.parser.tree.StencilTree;
import stencil.unittests.StencilTestCase;
import stencil.util.FileUtils;

public class TestMean extends StencilTestCase {
	final Module average;
	
	public TestMean() throws Exception {
		average = new Average();
	}
	
	
	private static Context makeContext(String name, String highOrderUse, String highOrderSpec) {
		Context c = new Context(name);
		
		try {
			StencilTree f = (StencilTree) ParseStencil.TREE_ADAPTOR.create(StencilParser.FUNCTION, "");
			StencilTree oa = (StencilTree) ParseStencil.TREE_ADAPTOR.create(StencilParser.OP_AS_ARG, "Mean");
			StencilTree spec = ParseStencil.specializerTree("[range:ALL]");

			StencilTree n = (StencilTree) ParseStencil.TREE_ADAPTOR.create(StencilParser.OP_NAME, "");
			ParseStencil.TREE_ADAPTOR.addChild(n, ParseStencil.TREE_ADAPTOR.create(StencilParser.ID, ""));
			ParseStencil.TREE_ADAPTOR.addChild(n, ParseStencil.TREE_ADAPTOR.create(StencilParser.ID, "Range"));
			ParseStencil.TREE_ADAPTOR.addChild(n, ParseStencil.TREE_ADAPTOR.create(StencilParser.ID, ""));

			
			ParseStencil.TREE_ADAPTOR.addChild(f, n);
			ParseStencil.TREE_ADAPTOR.addChild(f, spec);
			ParseStencil.TREE_ADAPTOR.addChild(f, oa);
			
			
			
			c = c.addCallSite(oa);
		} catch (Exception e) {throw new Error("Error in test setup",e);}
		return c;
	}
	
	public void testFullRange() throws Exception {
		StencilOperator meaner1 = average.instance("Mean", ParserConstants.EMPTY_SPECIALIZER);
		StencilOperator meaner = average.optimize(meaner1, makeContext("Mean", "Range", "[range: ALL]"));
		
		assertTrue("Mean not optmized to full range.", meaner instanceof Average.FullMean);
		
		FacetData fd = meaner.getOperatorData().defaultFacet();
		Invokeable map = meaner.getFacet(fd.name());
		Invokeable query = meaner.getFacet(fd.counterpart());
		
		int count =0;
		double sum =0;
		
		for (int i=0; i < 1000; i++) {
			sum = sum+i;
			count++;
			map.invoke(new Object[]{i});
			assertEquals(sum/count, query.invoke(new Object[0]));			
		}
	}
	
	public void testFullRangeInProgram() throws Exception {
		String source = FileUtils.readFile("./TestData/RegressionImages/Stocks/Stocks.stencil");
		StencilTree program = ParseStencil.programTree(source, stencil.adapters.java2D.Adapter.ADAPTER);
		for(StencilTree t: program.findAllDescendants(StencilParser.OPERATOR_PROXY)) {
			if (((OperatorProxy) t).getOperator() instanceof FullMean) {
				return;
			}
		}
		fail("Did not encounter FullMean instance when expected.");
	}
}
