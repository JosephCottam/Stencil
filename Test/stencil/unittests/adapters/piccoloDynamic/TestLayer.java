package stencil.unittests.adapters.piccoloDynamic;

import stencil.adapters.piccoloDynamic.DisplayLayer;
import stencil.adapters.piccoloDynamic.Adapter;

public class TestLayer extends stencil.unittests.adapters.TestLayer {

	public void testFind() throws Exception {
		DisplayLayer layer = (DisplayLayer) super.testFind(Adapter.INSTANCE);

		for (int i=0; i<100; i=i+1) {
			assertNotNull("Tuple with no pnode.", layer.find(Integer.toString(i)).getNode());
		}
	}

	public void testMake() throws Exception {
		super.testMake(Adapter.INSTANCE);
	}

	public void testRemove() throws Exception {
		super.testRemove(Adapter.INSTANCE);
	}

}
