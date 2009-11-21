package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.NoSuchElementException;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

public class SequenceStream implements TupleStream {
	private final long length;
	private long count;
	
	private final long start; 	
	private final long increment;	
	private final String name;	//Name of the stream
	
	private static final String[] FIELDS = new String[]{"VALUE"};
	                                               
	
	public SequenceStream(String name) {this(name, 0,1, -1);}
	
	public SequenceStream(String name, long start, long increment, long length) {
		this.name = name;
		this.start =start;
		this.increment = increment;
		this.length =length;
		count =0;
	}
	
	public Tuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		long value = (count * increment) + start;
		count++;
		
		return new PrototypedTuple(name, FIELDS, new Long[]{value});
	}

	public boolean ready() {return hasNext();}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public String[] getFields() {return FIELDS;}
}
