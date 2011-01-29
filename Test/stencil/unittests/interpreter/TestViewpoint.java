package stencil.unittests.interpreter;

import static stencil.adapters.java2D.Adapter.ADAPTER;
import stencil.interpreter.tree.*;
import stencil.module.ModuleCache;
import stencil.module.operator.wrappers.SyntheticOperator;
import stencil.parser.ParseStencil;
import stencil.parser.tree.StencilTree;
import stencil.testUtilities.StringUtils;
import static stencil.parser.string.StencilParser.*;

public class TestViewpoint extends stencil.unittests.StencilTestCase {
	public void testSyntheticOperator() throws Exception {
		StencilTree p = ParseStencil.programTree(StringUtils.getContents("./TestData/RegressionImages/SeeTest/SeeTest.stencil"), ADAPTER);
		
		StencilTree opDef= p.findAllDescendants(OPERATOR).get(0);
		
		SyntheticOperator op = new SyntheticOperator(ModuleCache.AD_HOC_NAME, opDef);
		SyntheticOperator vp = op.viewpoint();
		
		assertNotSame("Viewpoint did not return distinct entity.", op, vp);
	}
	
	public void testStateQuery() throws Exception {
		Program p = ParseStencil.program(StringUtils.getContents("./TestData/RegressionImages/AutoGuide/Flowers.stencil"), ADAPTER);
		StateQuery sq = p.canvas().guides()[0].stateQuery();
		StateQuery vp = sq.viewpoint();
		
		assertNotSame("Viewpoint did not return distinct entity.", sq, vp);
	}
}
