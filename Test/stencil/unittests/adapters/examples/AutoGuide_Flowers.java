package stencil.unittests.adapters.examples;

public class AutoGuide_Flowers extends ImageTest {
	public AutoGuide_Flowers(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/AutoGuide/",
				  "Flowers.stencil",
				  null,
				  null,
				  "flowers.txt",
				  "flowers.png", configs));
	}
}
