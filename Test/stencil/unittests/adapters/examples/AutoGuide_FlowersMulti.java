package stencil.unittests.adapters.examples;

public class AutoGuide_FlowersMulti extends ImageTest {
	public AutoGuide_FlowersMulti(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/AutoGuide/",
				  "Flowers_Multi.stencil",
				  null,
				  null,
				  "flowers_multi.txt",
				  "flowers_multi.png", configs));
	}
}
