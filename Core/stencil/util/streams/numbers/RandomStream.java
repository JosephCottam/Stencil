package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.NoSuchElementException;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

public class RandomStream implements TupleStream {
	private final long length; //How many tuples to produce
	private long count;			//How many tuples have been produced
	private final String name;	//Name of thes tream
	private final int size;		//How many fields per tuple
	private final String[] fields;//Field names
	
	public RandomStream(String name) {this(name, 2, Integer.MIN_VALUE);}
	
	public RandomStream(String name, int size, long length) {
		this.name = name;
		this.length = length;
		this.size =size;

		count =0;
		this.fields = fields(size);
	}
	
	public Tuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		count++;
		
		Double[] values = new Double[size];
		for (int i =0; i<size; i++) {values[i] = Math.random();}
		return new PrototypedTuple(name, fields, values);
	}

	public boolean ready() {return hasNext();}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public String[] getFields() {return fields;}
	
	private static String[] fields(int size) {
		String[] fields= new String[size];
		for (int i =0; i< size; i++) {
			fields[i] = "VALUE".concat(Integer.toString(i));
		}
		return fields;
	}
}
