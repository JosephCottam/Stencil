package stencil.unittests.parser.tree;

import stencil.parser.ParseStencil;
import stencil.parser.tree.*;
import stencil.parser.tree.util.Path;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;
import static stencil.adapters.java2D.Adapter.ADAPTER;
import static stencil.parser.string.StencilParser.*;

public class TestPath extends TestCase {
	private StencilTree tree1, tree2;
	
	public void setUp() throws Exception {
		stencil.Configure.loadProperties("./TestData/Stencil.properties");
		tree1 = ParseStencil.programTree(StringUtils.getContents("./TestData/RegressionImages/AutoGuide/Flowers.stencil"), ADAPTER);
		tree2 = ParseStencil.programTree(StringUtils.getContents("./TestData/RegressionImages/AutoGuide/Flowers.stencil"), ADAPTER);
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
		StencilTree r = tree1.find(LIST_LAYERS).getChild(0).findAllDescendants(CONSUMES).get(0).find(RULES_RESULT).getChild(0);
		test(r);
	}

	public void testGuide() {
		StencilTree r = tree1.find(LIST_LAYERS).getChild(0).findAllDescendants(GUIDE).get(0);
		test(r);
	}

	
	

}
