package stencil.unittests.adapters.examples;


public class AutoGuide_FlowersMulti extends ImageTest {
	public AutoGuide_FlowersMulti(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Flowers/",
				resultSpace("Flowers"),
				"AndersonFlowers-co.stencil",
				  null,
				  null,
				  "AndersonFlowers-co.png", configs));
	}
}
