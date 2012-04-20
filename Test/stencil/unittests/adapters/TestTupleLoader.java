
package stencil.unittests.adapters;

import stencil.adapters.TupleLoader;
import stencil.adapters.java2D.*;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.unittests.StencilTestCase;
import stencil.util.FileUtils;
import stencil.util.streams.txt.DelimitedParser;


public class TestTupleLoader extends StencilTestCase {
	public static final String STENCIL = "./TestData/RegressionImages/Sourceforge/Sourceforge.stencil";
	public static final String COORDS = "./TestData/RegressionImages/Sourceforge/suppliments/clusterCoords_fragment.coord";
	public static final String OVERLAY_SHORT = "./TestData/RegressionImages/Sourceforge/suppliments/project_troves_fragment.txt";
	public static final String OVERLAY_FULL = "./TestData/RegressionImages/Sourceforge/project_troves.txt";

	private Panel panel;

	@Override
	public void tearDown() {
		if (panel != null) {panel.dispose();}
	}
		
	public void testLoad() throws Exception {
		String ruleSource = FileUtils.readFile(STENCIL);
		
		panel = Adapter.ADAPTER.compile(ruleSource);

		DelimitedParser input = new DelimitedParser("NodePositions", COORDS, "\\s+", 3, true, 0);
		TupleLoader loader = new TupleLoader(panel, input);
		loader.load();

		
		synchronized(panel.getCanvas().getComponent().visLock) {
			TableShare share = ((Table) panel.getLayer("Nodes")).changeGenerations();
			share.simpleUpdate();
			((Table) panel.getLayer("Nodes")).merge(share);
			
			assertEquals("Unexpected number of items loaded.", 151, panel.getLayer("Nodes").viewpoint().size());
		}
	}

	public void testThread() throws Exception {
		String ruleSource = FileUtils.readFile(STENCIL);

		panel = Adapter.ADAPTER.compile(ruleSource);

		DelimitedParser input = new DelimitedParser("NodeAttributes", OVERLAY_FULL, "\\|", 2, true,1);
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
