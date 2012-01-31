package stencil.unittests.adapters.examples;


public class NodeLink extends ImageTest.Probablistc {
	public NodeLink(String[] configs) {
		super(.3, new TestRecord("./TestData/RegressionImages/NodeLink/",
				resultSpace("NodeLink"),
				"NodeLink.stencil",
				  null,
				  null,
				  "nodeLink.png", configs));
	}
}