package stencil.unittests.adapters.examples;


 
public class Jumpshot extends ImageTest {
	public Jumpshot(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/MPICommViz/",
									  "JumpShot.stencil",
									  null,
									  null,
									  "JumpShot.txt",
									  "JumpShot.png", 
									  configs));
	}
}
