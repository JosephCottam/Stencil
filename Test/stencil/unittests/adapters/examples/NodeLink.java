package stencil.unittests.adapters.examples;

public class NodeLink extends ImageTest {
	public NodeLink(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/NodeLink/",
				  "NodeLink.stencil",
				  null,
				  null,
				  "nodeLink.txt",
				  "nodeLink.png", configs));
	}
}