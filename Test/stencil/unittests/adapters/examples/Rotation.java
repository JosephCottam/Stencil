package stencil.unittests.adapters.examples;


public class Rotation extends ImageTest {
	public Rotation(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Rotation/",
				resultSpace("Rotation"),
				"Rotation.stencil",
				  null,
				  null,
				  "rotation.png", configs));
	}
}
