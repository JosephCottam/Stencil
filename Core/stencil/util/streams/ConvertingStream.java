package stencil.util.streams;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

/**Takes a tuple and converts the values to the specified types.
 */
public class ConvertingStream implements TupleStream {
	private final TupleStream source;
	private final Class[] types;
	
	public ConvertingStream(TupleStream source, Class[] types) {
		this.source = source;
		this.types = types;
	}

	
	@Override
	public boolean hasNext() {return source.hasNext();}

	@Override
	public SourcedTuple next() {
		SourcedTuple t = source.next();
		if (t == null) {return null;}
		
		Tuple data = t.getValues();
		Object[] values = new Object[data.size()];
		for (int i=0; i< values.length; i++) {
			values[i] = Converter.convert(data.get(i), types[i]);
		}
		Tuple typedData;
		if (data instanceof PrototypedTuple) {typedData = new PrototypedArrayTuple(((PrototypedTuple) data).prototype(), values);}
		else {typedData = new ArrayTuple(values);}
		return new SourcedTuple.Wrapper(t.getSource(), typedData);
	}

	@Override
	public void stop() {source.stop();}
	
	@Override
	public void remove() {throw new UnsupportedOperationException();}
	
	public TupleStream rootStream() {return source;}
}
