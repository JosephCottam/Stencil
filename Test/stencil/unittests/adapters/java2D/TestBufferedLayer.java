package stencil.unittests.adapters.java2D;

import stencil.adapters.java2D.Adapter;
import stencil.adapters.java2D.data.DoubleBufferLayer;

public class TestBufferedLayer extends stencil.unittests.adapters.TestLayer {
	public void testFind() throws Exception {
		DoubleBufferLayer layer = (DoubleBufferLayer) super.testFind(Adapter.INSTANCE);

		for (int i=0; i<100; i++) {
			assertNotNull("Expected tuple not found; ID:" + i, layer.find(Integer.toString(i)));
		}
	}

	public void testMake() throws Exception {
		super.testMake(Adapter.INSTANCE);
	}

	public void testRemove() throws Exception {
		super.testRemove(Adapter.INSTANCE);
	}

}
