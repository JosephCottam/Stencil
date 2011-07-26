package stencil.unittests.adapters.examples;

public class JungTree extends ImageTest {
	public JungTree(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/NodeLink/",
				  "JungTree.stencil",
				  null,
				  null,
				  "jungTree.txt",
				  "jungTree.png", configs));
	}
}