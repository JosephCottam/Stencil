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
	/**Tagging interface, indicates to the Stencil runtime that queuing is permitted.**/
	public static interface Queable {}
	
	
	private final TupleStream source;

	/**Maximum queue size.**/
	private final int prefetchSize;
	
	/**Prefetched tuples**/
	private Queue<SourcedTuple> tupleCache = new ConcurrentLinkedQueue();
	
	public QueuedStream(TupleStream source, int prefetch) {
		this.source = source;
		this.prefetchSize = prefetch;
	}
	
	@Override
	public boolean hasNext() {return !tupleCache.isEmpty() || source.hasNext();}

	@Override
	public SourcedTuple next() {
		if (tupleCache.isEmpty()) {
			for (int i=0; i< prefetchSize && source.hasNext(); i++) {
				SourcedTuple t = source.next();
				if (t  == null) {break;}
				tupleCache.add(t);
			}
		}
		return tupleCache.poll();
	}

	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
