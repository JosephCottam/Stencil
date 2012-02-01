package stencil.unittests.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.QueuedStream;
import junit.framework.TestCase;
import static stencil.unittests.util.streams.Util.*;

public class TestQueuedStream extends TestCase {
	public void testContentMatchThread() throws Exception {
		contentMatch(true);
	}
	
	public void testContentMatchUnthreaded() throws Exception {
		contentMatch(false);
	}
	
	private void contentMatch(boolean thread) throws Exception {
		TupleStream queued = new QueuedStream(binaryTrovesStream(), 100, thread);
		TupleStream unqueued = binaryTrovesStream();

		assertEquals("Before loading tuples hasNext is unmached.", unqueued.hasNext(), queued.hasNext());

		
		int n=0;
		while(queued.hasNext() && unqueued.hasNext()) {
			Tuple q,u;
			do {q = queued.next();} while (thread && q==null);	//Only needs to do null-spin in threaded case
			do {u = unqueued.next();} while (thread && u==null);
			assertEquals("Tuple " + n + " did not match", u,q);
			n++;
		}
		assertEquals("Loaded " + n + " tuples, but hasNext is unmached.", unqueued.hasNext(), queued.hasNext());		
	}
}
