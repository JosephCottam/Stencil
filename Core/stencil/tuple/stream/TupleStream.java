package stencil.tuple.stream;

import java.util.Iterator;

import stencil.tuple.SourcedTuple;

/**An iterator that generates tuples.
 *
 * HasNext should return false ONLY if the stream is definitely exhausted.
 * Calling 'next' should always return quickly. 
 * 'next' may return null if there is nothing right now, but may still be in the future.
 * */
public interface TupleStream extends Iterator<SourcedTuple> {
	
	/**Indicate that the stream should be terminated.
	 * This ensures that a stream using long-lived resources frees them quickly.
	 */
	public void stop();
}
