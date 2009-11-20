package stencil.unittests.util.streams;

import java.util.HashSet;
import junit.framework.*;
import stencil.tuple.Tuple;
import stencil.util.streams.feed.*;


public class TestTwitterTuples extends TestCase {
	//Data-mining feed (per http://apiwiki.twitter.com/REST+API+Documentation#publictimeline):
	//http://twitter.com/statuses/public_timeline.format (xml, json, rss, atom)
	public static final String TEST_FEED = "http://twitter.com/statuses/public_timeline.rss";

	public static final String FEED_NAME = "PublicTimeline";

	public void testSubscription() throws Exception {
		TwitterTuples feed = new TwitterTuples(FEED_NAME, TEST_FEED);
		HashSet<Tuple> seen = new HashSet();

		int counter = 100;
		while (feed.hasNext() && counter >0) {
			Tuple t = feed.next();
			assertFalse("Repeat tuple!", seen.contains(t));
			counter--;
		}
		assertEquals("Insufficient tuples found.", 0, counter);
	}
}
