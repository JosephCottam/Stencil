package stencil.unittests.adapters.examples;

public class AutoGuide_FlowersCross extends ImageTest {
	public AutoGuide_FlowersCross(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Flowers/",
				resultSpace("Flowers"),
				"AndersonFlowers-cross.stencil",
				  null,
				  null,
				  "flowers_cross.txt",
				  "flowers_cross.png", configs));
	}
}
