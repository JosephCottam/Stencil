package stencil.util.streams.numbers;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Random;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

@Description("Sequence of random numbers.")
@Stream(name="Random", spec="[seed:\"now\", length:-1, size:1]")
public class RandomStream implements TupleStream {
	public static final String SIZE = "size";
	public static final String LENGTH = "length";
	public static final String SEED = "seed";
	
	private final long length; //How many tuples to produce
	private long count;			//How many tuples have been produced
	private final String name;	//Name of the stream
	private final int size;		//How many fields per tuple
	private final TuplePrototype prototype;
	private final Random random;
	

	public RandomStream(String name, TuplePrototype proto, Specializer spec) {
		this(name,
			Converter.toInteger(spec.get(SIZE)),
			Converter.toInteger(spec.get(LENGTH)),
			spec.get(SEED).equals("now") ? System.currentTimeMillis() : Converter.toLong(spec.get(SEED)));
	}
	
	public RandomStream(String name, int size, long length, long seed) {
		this.name = name;
		this.length = length;
		this.size =size;
		this.random = new Random(seed);

		count =0;
		String[] fields = TuplePrototypes.defaultNames(size, "VALUE");
		prototype = new TuplePrototype(fields);
	}
	
	public SourcedTuple next() {
		if (length > 0 && count >= length) {throw new NoSuchElementException(format("Stream %1$s exhausted.", name));}
		count++;
		
		List<Double> values = new ArrayList(size);
		for (int i =0; i<size; i++) {values.add(random.nextDouble());}
		return new SourcedTuple.Wrapper(name, new PrototypedArrayTuple(prototype, values));
	}

	public boolean hasNext() {return (length < 0 || count < length);}

	public void remove() {throw new UnsupportedOperationException();}
	public void stop() {}
	public List<String> getFields() {return Arrays.asList(TuplePrototypes.getNames(prototype));}
}
