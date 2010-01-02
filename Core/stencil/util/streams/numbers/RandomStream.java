package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.List;

import stencil.parser.ParserConstants;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
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
		List<String> fields = TuplePrototypes.defaultNames(size, "VALUE");
		fields = new ArrayList(fields);
		fields.add(ParserConstants.SOURCE_FIELD);

		List<Class> types = TuplePrototypes.defaultTypes(fields.size());
		
		prototype = new SimplePrototype(fields, types);

	}
	
	public Tuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		count++;
		
		List<Double> values = new ArrayList(size);
		for (int i =0; i<size; i++) {values.add(Math.random());}
		return new PrototypedTuple(prototype, values);
	}

	public boolean ready() {return hasNext();}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public List<String> getFields() {return TuplePrototypes.getNames(prototype);}
}
