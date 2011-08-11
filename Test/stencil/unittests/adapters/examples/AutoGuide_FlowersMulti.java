package stencil.unittests.adapters.examples;

public class AutoGuide_FlowersMulti extends ImageTest {
	public AutoGuide_FlowersMulti(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Flowers/",
				  "AndersonFlowers-co.stencil",
				  null,
				  null,
				  "flowers-co.txt",
				  "flowers-co.png", configs));
	}
}
