package stencil.unittests.adapters.examples;


public class EpiC extends ImageTest {
	public EpiC(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Epic/",
				resultSpace("Epic"),
				"EpiC.stencil",
				  null,
				  null,
				  "EpiC.png", configs));
	}
}
