package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.List;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.tuple.prototype.TuplePrototypes;

public class RandomStream implements TupleStream {
	private final long length; //How many tuples to produce
	private long count;			//How many tuples have been produced
	private final String name;	//Name of thes tream
	private final int size;		//How many fields per tuple
	private final List<String> fields;//Field names
	private final List<Class> types;
	
	public RandomStream(String name) {this(name, 2, Integer.MIN_VALUE);}
	
	public RandomStream(String name, int size, long length) {
		this.name = name;
		this.length = length;
		this.size =size;

		count =0;
		this.fields = TuplePrototypes.defaultNames(size, "VALUE");
		this.types = TuplePrototypes.defaultTypes(size);
	}
	
	public Tuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		count++;
		
		List<Double> values = new ArrayList(size);
		for (int i =0; i<size; i++) {values.add(Math.random());}
		return new PrototypedTuple(name, fields, types, values);
	}

	public boolean ready() {return hasNext();}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public List<String> getFields() {return fields;}
}
