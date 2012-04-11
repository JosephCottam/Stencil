package stencil.unittests.interpreter;

import static stencil.adapters.java2D.Adapter.ADAPTER;
import stencil.interpreter.tree.*;
import stencil.module.operator.wrappers.SyntheticOperator;
import stencil.parser.ParseStencil;
import stencil.parser.ParserConstants;
import stencil.parser.tree.StencilTree;
import stencil.util.FileUtils;
import static stencil.parser.string.StencilParser.*;

public class TestViewpoint extends stencil.unittests.StencilTestCase {
	public void testSyntheticOperator() throws Exception {
		StencilTree p = ParseStencil.programTree(FileUtils.readFile("./TestData/RegressionImages/SeeTest/SeeTest.stencil"), ADAPTER);
		
		StencilTree opDef= p.findAllDescendants(OPERATOR).get(0);
		
		SyntheticOperator op = new SyntheticOperator(ParserConstants.STAND_IN_GROUP, opDef);
		SyntheticOperator vp = op.viewpoint();
		
		assertNotSame("Viewpoint did not return distinct entity.", op, vp);
	}
	
	public void testStateQuery() throws Exception {
		Program p = ParseStencil.program(FileUtils.readFile("./TestData/RegressionImages/Flowers/AndersonFlowers.stencil"), ADAPTER);
		StateQuery sq = p.allGuides()[0].stateQuery();
		StateQuery vp = sq.viewpoint();
		
		assertNotSame("Viewpoint did not return distinct entity.", sq, vp);
	}
}
