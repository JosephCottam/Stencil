package stencil.unittests.adapters.examples;

public class RadialTree extends ImageTest {
	public RadialTree(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/NodeLink/",
				resultSpace("NodeLink"),
				"RadialTree.stencil",
				  null,
				  null,
				  "radialTree.png", configs));
	}
}