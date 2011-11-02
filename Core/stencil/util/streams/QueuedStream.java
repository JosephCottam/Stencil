package stencil.util.streams;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import stencil.tuple.SourcedTuple;
import stencil.tuple.stream.TupleStream;

/**Performs caching for tuple streams.  
 * Will return tuples in the same order as they are produced but performs bulk collection of tuples
 * so production times may differ from the unwrapped queue (ergo, not all tuple streams should be wrapped).
 * 
 * @author jcottam
 *
 */
public class QueuedStream implements TupleStream {
	/**How large should the queue be at its maximum**/
	public static int DEFAULT_QUEUE_SIZE = 50;
		
	/**Should queuing be done in a separate thread?**/
	public static boolean THREAD = false;			
	
	/**Tagging interface, indicates to the Stencil runtime that queuing is permitted.**/
	public static interface Queable {}
	
	private final class QueueLoader implements Runnable {
		public void run() {
			while (source != null) {
				if (!source.hasNext()) {break;}
				int getMore = prefetchSize - tupleCache.size(); //queue.size is not necessarily constant time, so the value is grabbed and used in the inner loop
				boolean doSleep = getMore < 10;					//Not much to get, take a quick break
				for (int i=0; i<getMore; i++) {loadQueue();}
				if (doSleep) {Thread.yield();}
			}
		}
	}	
	
	private final TupleStream source;

	/**Maximum queue size.**/
	private final int prefetchSize;
	
	/**Pre-fetched tuples**/
	private final Queue<SourcedTuple> tupleCache = new ConcurrentLinkedQueue();
	
	private final Thread loader;
	
	public QueuedStream(TupleStream source) {this(source, DEFAULT_QUEUE_SIZE, THREAD);}
	public QueuedStream(TupleStream source, int prefetch) {this(source, prefetch, THREAD);}
	public QueuedStream(TupleStream source, int prefetch, boolean thread) {
		this.source = source;
		this.prefetchSize = prefetch;
		if (thread) {
			loader = new Thread(new QueueLoader(), "Queued Tuple Loader");
			loader.start();
		} else {
			loader = null;
		}
		
	}

	
	@Override
	public boolean hasNext() {
		//if (!tupleCache.isEmpty()) {return true;}
		return !tupleCache.isEmpty() || source.hasNext();
	}

	@Override
	public SourcedTuple next() {
		if (loader == null && tupleCache.isEmpty()) {loadQueue();}
		return tupleCache.poll();
	}

	/**Places as many items as possible into the queue.
	 * Will load until (a) the queue is full
	 * 	               (b) the source returns null
	 *                 (c) the source  hasNext returns false
	 **/
	private void loadQueue() {
		for (int i=0; i< prefetchSize && source.hasNext(); i++) {
			SourcedTuple t = source.next();
			if (t  == null) {break;}
			tupleCache.add(t);
		}
	}


	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
