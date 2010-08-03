package stencil.unittests.parser.tree.util;

import stencil.parser.ParseStencil;
import stencil.parser.tree.*;
import stencil.parser.tree.util.Path;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;
import static stencil.adapters.java2D.Adapter.ADAPTER;

public class TestPath extends TestCase {
	private Program tree1, tree2;
	
	public void setUp() throws Exception {
		stencil.Configure.loadProperties("./TestData/Stencil.properties");
		tree1 = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/AutoGuide/Flowers.stencil"), ADAPTER);
		tree2 = ParseStencil.parse(StringUtils.getContents("./TestData/RegressionImages/AutoGuide/Flowers.stencil"), ADAPTER);
	}
	
	public void tearDown() {
		tree1 = null;
		tree2 = null;
	}
	
	//Perform an actual test
	//Assumes the target comes from tree1
	private void test(StencilTree target) {
		Path p = new Path(target);
		
		assertSame(target, p.apply(tree1));	
		assertEquals(target.getClass(), p.apply(tree2).getClass());	
	}	
	
	
	public void testRoot() {test(tree1);}

	public void testRule() {
		Rule r = tree1.getLayers().get(0).getGroups().get(0).getResultRules().get(0);
		test(r);
	}

	public void testGuide() {
		Rule r = tree1.getLayers().get(0).getGroups().get(0).getResultRules().get(0);
		test(r);
	}

	
	

}
