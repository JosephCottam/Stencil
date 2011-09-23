package stencil.unittests.adapters.examples;

public class Registration extends ImageTest {
	public Registration(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Registration/",
				resultSpace("Registration"),
				"Registration.stencil",
				  null,
				  null,
				  "registration.png", configs));
	}
}
