package stencil.unittests.adapters.piccoloDynamic;

import stencil.adapters.piccoloDynamic.Adapter;

public class TestApplicationEngine extends stencil.unittests.interpreter.TestInterpreter {

	public void testRegisterFails() throws Exception{
		super.testRegisterFails(Adapter.INSTANCE);
	}

	public void testSimpleLines() throws Exception {
		super.testSimpleLines(Adapter.INSTANCE);
	}

}
