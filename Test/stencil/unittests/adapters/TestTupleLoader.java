package stencil.unittests.adapters;

import stencil.adapters.TupleLoader;
import stencil.adapters.java2D.*;
import stencil.parser.tree.*;
import stencil.parser.string.ParseStencil;
import stencil.testUtilities.StringUtils;
import stencil.util.streams.txt.DelimitedParser;

import junit.framework.TestCase;


public class TestTupleLoader extends TestCase {
	public static final String STENCIL = "./TestData/RegressionImages/Sourceforge/Sourceforge.stencil";
	public static final String COORDS = "./TestData/RegressionImages/Sourceforge/suppliments/clusterCoords_fragment.coord";
	public static final String OVERLAY_SHORT = "./TestData/RegressionImages/Sourceforge/suppliments/project_troves_fragment.txt";
	public static final String OVERLAY_FULL = "./TestData/RegressionImages/Sourceforge/project_troves.txt";


	public void testLoad() throws Exception {
		String ruleSource = StringUtils.getContents(STENCIL);
		
		Program program = ParseStencil.parse(ruleSource, Adapter.INSTANCE);
		Panel panel = Adapter.INSTANCE.generate(program);

		DelimitedParser input = new DelimitedParser("NodePositions", "ID X Y", COORDS, "\\s+", true, 0);
		TupleLoader loader = new TupleLoader(panel, input);
		loader.load();

		assertEquals("Unexpected number of items loaded.", 151, panel.getLayer("Nodes").size());
	}

	public void testThread() throws Exception {
		String ruleSource = StringUtils.getContents(STENCIL);

		Program program = ParseStencil.parse(ruleSource, Adapter.INSTANCE);
		Panel panel = Adapter.INSTANCE.generate(program);

		DelimitedParser input = new DelimitedParser("NodeAttributes", "ID|ATT", OVERLAY_FULL, "\\|", true,1);
		TupleLoader loader = new TupleLoader(panel, input);

		Thread thread = new Thread(loader);

		assertTrue(loader.unstarted());
		thread.start();
		Thread.sleep(100);
		assertTrue(loader.isRunning());
		thread.join();
		assertTrue(loader.isStopped());

		//Nothing should load because we don't load the other data.  The filter's never pass, so nothing ever loads.
		assertEquals("Unexpected number of items loaded.", 0, panel.getLayer("Overlay").size());
	}
}
