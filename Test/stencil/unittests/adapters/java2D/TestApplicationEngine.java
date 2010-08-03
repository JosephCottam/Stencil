package stencil.unittests.adapters.java2D;

import stencil.adapters.java2D.Adapter;

public class TestApplicationEngine extends stencil.unittests.interpreter.TestInterpreter {
	public void testSimpleLines() throws Exception {
		super.testSimpleLines(Adapter.ADAPTER);
	}


	public void testRegisterFails() throws Exception{
		super.testRegisterFails(Adapter.ADAPTER);
	}

}
