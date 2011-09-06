package stencil.unittests.adapters.examples;

public class AutoGuide_Flowers extends ImageTest {
	public AutoGuide_Flowers(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Flowers/",
				resultSpace("Flowers"),
				"AndersonFlowers.stencil",
				  null,
				  null,
				  "AndersonFlowers.txt",
				  "AndersonFlowers.png", configs));
	}
}
