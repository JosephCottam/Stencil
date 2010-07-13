package stencil.unittests.adapters.java2D;

import stencil.adapters.java2D.Adapter;
import stencil.adapters.java2D.Panel;

public class TestGenerate extends stencil.unittests.adapters.TestGenerator {
	private Panel panel;
	
	public void tearDown() {
		if (panel != null) {panel.dispose();}
	}
	
	public void testGenerate() throws Exception{
		panel = (Panel) super.testGenerate(Adapter.INSTANCE);
		//Make sure all parts are present
		assertNotNull(panel.getCanvas());
	}

	public void testMakeLoader() throws Exception {
		super.testMakeLoader(Adapter.INSTANCE);
	}

}
