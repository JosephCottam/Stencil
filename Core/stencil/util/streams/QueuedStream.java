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
	private final class QueueLoader extends Thread {
		private volatile boolean stop = false;
		
		public QueueLoader() {
			super("Queued Tuple Loader");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
			while (!stop && source != null) {
				if (!source.hasNext()) {break;}
				int getMore = prefetchSize - tupleCache.size(); //queue.size is not necessarily constant time, so the value is grabbed and used in the inner loop
				boolean doSleep = getMore < 10;					//Not much to get, take a quick break
				loadQueue();
				if (doSleep) {Thread.yield();}
			}
		}
		
		public void signalStop() {stop = true;}
	}	
	
	protected final TupleStream source;

	/**Maximum queue size.**/
	protected final int prefetchSize;
	
	/**Storage for pre-fetched tuple cache**/
	protected final Queue<SourcedTuple> tupleCache = new ConcurrentLinkedQueue();
	
	private final QueueLoader loader;
	
	public QueuedStream(TupleStream source, int prefetch, boolean thread) {
		this.source = source;
		this.prefetchSize = prefetch;
		if (thread) {
			loader = new QueueLoader();
			loader.setDaemon(true);
			loader.start();
		} else {
			loader = null;
		}
		
	}

	@Override
	public void stop() {
		if (loader != null) {loader.signalStop();}
		source.stop();
	}
	
	@Override
	public boolean hasNext() {
		if (loader == null && tupleCache.isEmpty()) {loadQueue();} 	//Pre-fetch if there is no loader...
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
	protected void loadQueue() {
		for (int i=0; i< prefetchSize && source.hasNext(); i++) {
			SourcedTuple t = source.next();
			if (t  == null) {break;}
			tupleCache.add(t);
		}
	}


	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
