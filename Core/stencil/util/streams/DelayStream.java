package stencil.util.streams;

import stencil.tuple.SourcedTuple;
import stencil.tuple.stream.TupleStream;

/**Will cause a delay before returning a tuple.
 * Useful for demonstration purposes. 
 * 
 * @author jcottam
 *
 */
public class DelayStream implements TupleStream {

	private final TupleStream source;

	/**Milliseconds to delay.*/
	private final int delay;
		
	public DelayStream(TupleStream source, int delay) {
		this.source=source;
		this.delay = delay;
	}
	
	
	@Override
	public boolean hasNext() {return source.hasNext();}

	@Override
	public SourcedTuple next() {
		try {Thread.sleep(delay);}
		catch (InterruptedException e) {/**Ignored**/}
		return source.next();
	}

	@Override
	public void stop() {source.stop();}
	
	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
