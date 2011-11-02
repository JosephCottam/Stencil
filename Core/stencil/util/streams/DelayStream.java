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
	/**Milliseconds to delay.*/
	public static int DELAY = 0;

	private final TupleStream source;
		
	public DelayStream(TupleStream source) {this.source=source;}
	
	
	@Override
	public boolean hasNext() {return source.hasNext();}

	@Override
	public SourcedTuple next() {
		if (DELAY > 0) {
			try {Thread.sleep(DELAY);}
			catch (InterruptedException e) {/**Ignored**/}
		}
		return source.next();
	}

	@Override
	public void stop() {source.stop();}
	
	@Override
	public void remove() {throw new UnsupportedOperationException();}
}
