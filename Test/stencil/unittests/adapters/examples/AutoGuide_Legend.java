package stencil.unittests.adapters.examples;

public class AutoGuide_Legend extends ImageTest {

	public AutoGuide_Legend(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/AutoGuide/",
				resultSpace("AutoGuide"),
				  "Legend.stencil",
				  null,
				  null,
				  "legend.png", configs));
	}

}
