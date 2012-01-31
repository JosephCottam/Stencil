package stencil.unittests.adapters.examples;


public class RadialTree extends ImageTest.Probablistc {
	public RadialTree(String[] configs) {
		super(.3, new TestRecord("./TestData/RegressionImages/NodeLink/",
				resultSpace("NodeLink"),
				"RadialTree.stencil",
				  null,
				  null,
				  "radialTree.png", configs));
	}
}