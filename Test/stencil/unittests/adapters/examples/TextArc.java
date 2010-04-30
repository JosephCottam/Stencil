package stencil.unittests.adapters.examples;

public class TextArc extends RestrictedImageTest {
	public TextArc(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/TextArc/",
									  "textArc.stencil",
									  null,
									  null,
									  "AliceInWonderland.txt",
									  "AliceInWonderland.png", configs));
	}
}
