package stencil.unittests.adapters.examples;

public class SquareShells extends ImageTest {
	public SquareShells(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/SquareShells/",
				resultSpace("SquareShells"),
				"SquareShells.stencil",
				  null,
				  null,
				  "SquareShells.png", 
				  configs));
	}	
}
