package stencil.unittests.adapters.examples;

public class Sourceforge extends ImageTest {
	public Sourceforge(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Sourceforge/",
									  "Sourceforge.stencil",
									  null,
									  null,
									  "Sourceforge.txt",
									  "Sourceforge.png", 
									  configs));
	}
	
}
