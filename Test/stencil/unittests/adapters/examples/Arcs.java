package stencil.unittests.adapters.examples;


public class Arcs extends ImageTest {
	public Arcs(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/SimpleLines/",
				resultSpace("SimpleLines"),
				  "Arcs.stencil",
				  null,
				  null,
				  "arcs.png", configs));
	}
}
