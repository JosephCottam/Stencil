package stencil.util.streams;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import stencil.tuple.SourcedTuple;
import stencil.tuple.TupleStream;

/**Performs caching for tuple streams.  
 * Will return tuples in the same order as they are produced but performs bulk collection of tuples
 * so production times may differ from the unwrapped queue (ergo, not all tuple streams should be wrapped).
 * 
 * @author jcottam
 *
 */
public class QueuedStream implements TupleStream {
	public static final boolean THREAD = false;
	
	/**Tagging interface, indicates to the Stencil runtime that queuing is permitted.**/
	public static interface Queable {}
	
	private final class QueueLoader implements Runnable {
		public void run() {
			while (source!= null && source.hasNext()) {
				synchronized(tupleCache) {
					if (tupleCache.size() < prefetchSize/2) {
						loadQueue();
					}
					try {tupleCache.wait();}
					catch (InterruptedException e) {}
				}
			}
		}
	}	
	
	private final TupleStream source;

	/**Maximum queue size.**/
	private final int prefetchSize;
	
	/**Prefetched tuples**/
	private Queue<SourcedTuple> tupleCache = new ConcurrentLinkedQueue();
	
	private final Thread loader;
	
	public QueuedStream(TupleStream source, int prefetch, boolean thread) {
		this.source = source;
		this.prefetchSize = prefetch;
		if (thread) {
			loader = new Thread(new QueueLoader());
			loader.start();
		} else {
			loader = null;
		}
		
	}

	public QueuedStream(TupleStream source, int prefetch) {this(source, prefetch, THREAD);}
	
	@Override
	public boolean hasNext() {return !tupleCache.isEmpty() || source.hasNext();}

	@Override
	public SourcedTuple next() {
		if (tupleCache.isEmpty()) {loadQueue();}
		if (tupleCache.size() < prefetchSize/2) {synchronized(tupleCache) {tupleCache.notify();}}
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
