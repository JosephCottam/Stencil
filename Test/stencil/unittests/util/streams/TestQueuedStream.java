package stencil.unittests.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.QueuedStream;
import junit.framework.TestCase;
import static stencil.unittests.util.streams.Util.*;

public class TestQueuedStream extends TestCase {
	private boolean QUEUED_RESTORE;
	
	public void setUp() {QUEUED_RESTORE = QueuedStream.THREAD;}
	public void tearDown() {QueuedStream.THREAD = QUEUED_RESTORE;}
		
	public void testContentMatchThread() throws Exception {
		QueuedStream.THREAD = true;
		contentMatch();
	}
	
	public void testContentMatchUnthreaded() throws Exception {
		QueuedStream.THREAD = false;
		contentMatch();
	}
	
	private void contentMatch() throws Exception {
		TupleStream queued = new QueuedStream(binaryTrovesStream(), 100);
		TupleStream unqueued = binaryTrovesStream();

		assertEquals("Before loading tuples hasNext is unmached.", unqueued.hasNext(), queued.hasNext());

		
		int n=0;
		while(queued.hasNext() && unqueued.hasNext()) {
			Tuple q = queued.next();
			Tuple u = unqueued.next();
			assertEquals("Tuple " + n + " did not match", u,q);
			n++;
		}
		assertEquals("Loaded " + n + " tuples, but hasNext is unmached.", unqueued.hasNext(), queued.hasNext());		
	}
}
