package stencil.unittests.parser.tree;

import stencil.parser.ParseStencil;
import stencil.parser.tree.*;
import stencil.util.FileUtils;
import junit.framework.TestCase;
import static stencil.adapters.java2D.Adapter.ADAPTER;
import static stencil.parser.string.StencilParser.*;

public class TestPath extends TestCase {
	private StencilTree tree1, tree2;
	
	@Override
	public void setUp() throws Exception {
		stencil.Configure.loadProperties("./TestData/Stencil.properties");
		tree1 = ParseStencil.programTree(FileUtils.readFile("./TestData/RegressionImages/Flowers/AndersonFlowers.stencil"), ADAPTER);
		tree2 = ParseStencil.programTree(FileUtils.readFile("./TestData/RegressionImages/Flowers/AndersonFlowers.stencil"), ADAPTER);
	}
	
	@Override
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
