package stencil.unittests.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.util.streams.QueuedStream;
import junit.framework.TestCase;
import static stencil.unittests.util.streams.Util.*;

public class TestQueuedStream extends TestCase {
	public void testContentMatch() throws Exception {
		TupleStream queued = new QueuedStream(binaryTrovesStream(), 100);
		TupleStream unqueued = binaryTrovesStream();

		assertEquals("Before loading tuples hasNext is unmached.", queued.hasNext(), unqueued.hasNext());

		
		int n=0;
		while(queued.hasNext() && unqueued.hasNext()) {
			Tuple q = queued.next();
			Tuple u = unqueued.next();
			assertEquals("Tuple " + n + " did not match", q,u);
			n++;
		}
		assertEquals("Loaded " + n + " tuples, but hasNext is unmached.", queued.hasNext(), unqueued.hasNext());
	}
}
