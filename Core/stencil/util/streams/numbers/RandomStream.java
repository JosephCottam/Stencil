package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.List;

import stencil.tuple.SourcedTuple;
import stencil.tuple.TupleStream;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

public class RandomStream implements TupleStream {
	private final long length; //How many tuples to produce
	private long count;			//How many tuples have been produced
	private final String name;	//Name of thes tream
	private final int size;		//How many fields per tuple
	private final TuplePrototype prototype;
	
	public RandomStream(String name) {this(name, 2, Integer.MIN_VALUE);}
	
	public RandomStream(String name, int size, long length) {
		this.name = name;
		this.length = length;
		this.size =size;

		count =0;
		String[] fields = TuplePrototypes.defaultNames(size, "VALUE");
		prototype = new SimplePrototype(fields);
	}
	
	public SourcedTuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		count++;
		
		List<Double> values = new ArrayList(size);
		for (int i =0; i<size; i++) {values.add(Math.random());}
		return new SourcedTuple.Wrapper(name, new PrototypedTuple(prototype, values));
	}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public List<String> getFields() {return Arrays.asList(TuplePrototypes.getNames(prototype));}
}
