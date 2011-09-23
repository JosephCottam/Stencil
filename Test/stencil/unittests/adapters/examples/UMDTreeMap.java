package stencil.unittests.adapters.examples;

public class UMDTreeMap extends ImageTest {
	public UMDTreeMap(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/NodeLink/",
				resultSpace("NodeLink"),
				"UMDTreeMap.stencil",
				  null,
				  null,
				  "UMDTreeMap.png", configs));
	}
}