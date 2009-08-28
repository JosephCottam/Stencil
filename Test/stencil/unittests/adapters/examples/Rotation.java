package stencil.unittests.adapters.examples;

public class Rotation extends ImageTest {
	public Rotation(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Rotation/",
									  "Rotation.stencil",
									  null,
									  null,
									  "rotation.txt",
									  "rotation.png", configs));
	}
}
