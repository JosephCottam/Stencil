package stencil.util.streams.numbers;

import java.util.Arrays;

import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;

public class SequenceStream implements TupleStream {
	private double current;
	
	private final double increment;	
	private final double stop;
	private final String name;	//Name of the stream

	private static final TuplePrototype PROTOTYPE = new SimplePrototype(new String[]{"VALUE"}, new Class[]{Long.class});
	                                               
	public SequenceStream(String name) {this(name, 0,1, 1);}
	
	public SequenceStream(String name, double start, double increment, double stop) {
		this.name = name;
		this.stop = stop;
		this.increment = increment;
		current=start-increment;
		
		if (increment ==0) {throw new IllegalArgumentException("Increment cannot be 0.");}
		else if ((increment >0 && start > stop) 
				|| (increment < 0 && start < stop)) {throw new IllegalArgumentException("Increment must move start towards stop.");}
	}
	
	public SourcedTuple next() {
		current = current + increment;
		return new SourcedTuple.Wrapper(name, new PrototypedTuple(PROTOTYPE, Arrays.asList(current)));
	}

	public boolean hasNext() {return increment>0 ? current < stop : current > stop;}

	public void remove() {throw new UnsupportedOperationException();}
}
