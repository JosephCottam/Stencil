package stencil.unittests.adapters.examples;

public class AutoGuide_Rose extends ImageTest {
	public AutoGuide_Rose(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Nightingale/",
				resultSpace("Nightingale"),
				"Nightingale.stencil",
				  null,
				  null,
				  "nightingale.png", configs));
	}
}
