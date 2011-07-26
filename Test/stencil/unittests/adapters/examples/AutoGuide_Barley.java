package stencil.unittests.adapters.examples;

public class AutoGuide_Barley extends ImageTest {
	public AutoGuide_Barley(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/AutoGuide/",
				  "BarleyCell.stencil",
				  null,
				  null,
				  "barleyCell.txt",
				  "barleyCell.png", configs));
	}
}
