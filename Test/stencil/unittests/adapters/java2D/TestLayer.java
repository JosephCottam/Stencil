package stencil.unittests.adapters.java2D;

import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.Adapter;

public class TestLayer extends stencil.unittests.adapters.TestLayer {

	public void testFind() throws Exception {
		DisplayLayer layer = (DisplayLayer) super.testFind(Adapter.INSTANCE);

		for (int i=0; i<100; i=i+1) {
			assertNotNull("Tuple with no pnode.", layer.find(Integer.toString(i)));
		}
	}

	public void testMake() throws Exception {
		super.testMake(Adapter.INSTANCE);
	}

	public void testRemove() throws Exception {
		super.testRemove(Adapter.INSTANCE);
	}

}
