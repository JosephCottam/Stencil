package stencil.unittests.adapters.examples;

public class AutoGuide_TweetCount extends ImageTest {
	public AutoGuide_TweetCount(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/AutoGuide/",
				resultSpace("AutoGuide"),
				"TweetCountLocal.stencil",
				  null,
				  null,
				  "tweetCount.txt",
				  "tweetCount.png", configs));
	}
}
