package stencil.unittests.adapters.examples;

public class SimpleLines extends ImageTest {
	public SimpleLines(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/SimpleLines/",
				resultSpace("SimpleLines"),
				"Lines.stencil",
				  null,
				  null,
				  "lines.txt",
				  "lines.png", configs));
	}
}
