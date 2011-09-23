package stencil.unittests.adapters.examples;

public class AutoGuide_Axis extends ImageTest {
	public AutoGuide_Axis(String[] configs) {
		super(new TestRecord(
				"./TestData/RegressionImages/AutoGuide/",
				resultSpace("AutoGuide"),
				  "Axis.stencil",
				  null,
				  null,
				  "axis.png", configs));
	}
}
