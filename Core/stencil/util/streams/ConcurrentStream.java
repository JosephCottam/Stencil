package stencil.util.streams;

import stencil.tuple.SourcedTuple;
import stencil.tuple.stream.TupleStream;

import java.util.Arrays;
import java.util.List;

/**Prototype concurrent loading stream.
 * 
 * All streams are given a round-robing chance to provide data.
 * Since the first stream passed in is given the first chance
 * to load data, it is somewhat dependent on the order.  However,
 * all other streams will be given a chance before that one is
 * given another chance.
 *
 */
public class ConcurrentStream implements TupleStream {
	protected List<TupleStream> streams;

	protected int offset;
	
	public ConcurrentStream(TupleStream... streams) {
		this.streams = Arrays.asList(streams);
	}

	public ConcurrentStream(List<TupleStream> streams) {
		this.streams=streams;
		offset = 0;
	}

	public SourcedTuple next() {
		SourcedTuple nv = null;
		int startOffset = offset;
		
		//TODO: This is busy waiting...we should go with a listener architecture.
		do {
			nv = streams.get(offset).next();	
			incrimentOffset();
		} while (nv==null && offset != startOffset);
		
		return nv;
	}

	public boolean hasNext() {
		int initial = offset;
		do {
			if (streams.get(offset).hasNext()) {return true;}	
			incrimentOffset();
		} while (offset != initial);
		
		return false;
	}

	public void remove() {throw new UnsupportedOperationException("Remove not supported on ConcurrentStream.");}
	
	private void incrimentOffset() {offset = (offset+1) % streams.size();}
}
