package stencil.unittests.adapters.examples;

public class Stocks extends ImageTest {
	public Stocks(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Stocks/",
				resultSpace("Stocks"),
				"Stocks.stencil",
									  null,
									  null,
									  "stocks.txt",
									  "stocks.png", configs));
	}
}
