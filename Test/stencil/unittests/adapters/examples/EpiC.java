package stencil.unittests.adapters.examples;

public class EpiC extends ImageTest {
	public EpiC(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Epic/",
									  "EpiC.stencil",
									  null,
									  null,
									  "EpiC.txt",
									  "EpiC.png", configs));
	}
}
