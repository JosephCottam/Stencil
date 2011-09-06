package stencil.unittests.adapters.examples;

public class TextArc extends ImageTest {
	public TextArc(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/TextArc/",
				resultSpace("TextArc"),
				"textArc.stencil",
									  null,
									  null,
									  "TextArcAlice.txt",
									  "TextArcAlice.png", configs));
	}
}
