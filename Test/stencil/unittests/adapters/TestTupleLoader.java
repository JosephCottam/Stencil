package stencil.unittests.adapters;

import stencil.adapters.TupleLoader;
import stencil.adapters.java2D.*;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Program;
import stencil.testUtilities.StringUtils;
import stencil.unittests.StencilTestCase;
import stencil.util.streams.txt.DelimitedParser;


public class TestTupleLoader extends StencilTestCase {
	public static final String STENCIL = "./TestData/RegressionImages/Sourceforge/Sourceforge.stencil";
	public static final String COORDS = "./TestData/RegressionImages/Sourceforge/suppliments/clusterCoords_fragment.coord";
	public static final String OVERLAY_SHORT = "./TestData/RegressionImages/Sourceforge/suppliments/project_troves_fragment.txt";
	public static final String OVERLAY_FULL = "./TestData/RegressionImages/Sourceforge/project_troves.txt";

	private Panel panel;

	public void tearDown() {
		if (panel != null) {panel.dispose();}
	}
	
	public void testLoad() throws Exception {
		String ruleSource = StringUtils.getContents(STENCIL);
		
		Program program = ParseStencil.program(ruleSource, Adapter.ADAPTER);
		panel = Adapter.ADAPTER.generate(program);

		DelimitedParser input = new DelimitedParser("NodePositions", "ID X Y", COORDS, "\\s+", true, 0);
		TupleLoader loader = new TupleLoader(panel, input);
		loader.load();

		
		synchronized(panel.getCanvas().getComponent().visLock) {
			((DoubleBufferLayer) panel.getLayer("Nodes")).changeGenerations();
			assertEquals("Unexpected number of items loaded.", 151, panel.getLayer("Nodes").viewpoint().size());
		}
	}

	public void testThread() throws Exception {
		String ruleSource = StringUtils.getContents(STENCIL);

		Program program = ParseStencil.program(ruleSource, Adapter.ADAPTER);
		panel = Adapter.ADAPTER.generate(program);

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
		assertEquals("Unexpected number of items loaded.", 0, panel.getLayer("Overlay").viewpoint().size());
	}
}
