package stencil.unittests.adapters.examples;

public class SeeTest extends ImageTest {
	public SeeTest(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/SeeTest/",
									  "SeeTest.stencil",
									  null,
									  null,
									  "SeeTest.txt",
									  "SeeTest.png", configs));
	}
}
