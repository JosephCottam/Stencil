package stencil.unittests.adapters.examples;

public class Sourceforge extends ImageTest {
	public Sourceforge(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Sourceforge/",
				resultSpace("Sourceforge"),
				"Sourceforge.stencil",
									  null,
									  null,
									  "Sourceforge.txt",
									  "Sourceforge.png", 
									  configs));
	}
	
}
