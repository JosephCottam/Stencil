package stencil.unittests.adapters.examples;


public class JungTree extends ImageTest.Probablistc {
	public JungTree(String[] configs) {
		super(.3, new TestRecord("./TestData/RegressionImages/NodeLink/",
				resultSpace("NodeLink"),
				"JungTree.stencil",
				  null,
				  null,
				  "jungTree.png", configs));
	}
}