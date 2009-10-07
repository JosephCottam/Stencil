package stencil.unittests.adapters.examples;

public class Poverty extends ImageTest {
	public Poverty(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Poverty/",
									  "poverty.stencil",
									  null,
									  null,
									  "poverty.txt",
									  "poverty.png", configs));
	}
}
