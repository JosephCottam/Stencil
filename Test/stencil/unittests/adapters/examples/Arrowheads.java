package stencil.unittests.adapters.examples;

public class Arrowheads extends ImageTest {
	public Arrowheads(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/SimpleLines/",
				  "Arrowheads.stencil",
				  null,
				  null,
				  "arrowheads.txt",
				  "arrowheads.png", configs));
	}
}