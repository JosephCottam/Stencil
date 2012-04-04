package stencil.modules.java;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

@Description("Sequence of numbers determined by a mathematical function.")
@Stream(name="Generator", spec="[f:\"x\", start:0, step:.01, stop:1]")
public class GeneratorStream implements TupleStream {
	public static final String START = "start";
	public static final String STOP = "stop";
	public static final String STEP = "step";
	public static final String F = "f";
	
	private double current;
	
	private final double increment;	
	private final double stop;
	private final String name;	//Name of the stream

	
	public GeneratorStream(String name, TuplePrototype proto, Specializer spec) {
		this(name,
			Converter.toDouble(spec.get(START)),
			Converter.toDouble(spec.get(STEP)),
			Converter.toDouble(spec.get(STOP)));
		validate(proto);
	}
	
	public GeneratorStream(String name, double start, double increment, double stop) {
		this.name = name;
		this.stop = stop;
		this.increment = increment;
		current=start-increment;
		
		if (increment ==0) {throw new IllegalArgumentException("Increment cannot be 0.");}
		else if ((increment >0 && start > stop) 
				|| (increment < 0 && start < stop)) {throw new IllegalArgumentException("Increment must move start towards stop.");}
	}
	
	private void validate(TuplePrototype proto) {
		if (proto.size() != 1) {throw new IllegalArgumentException("Can only produce singleton tuples.");}
	}
	
	public SourcedTuple next() {
		current = current + increment;
		return new SourcedTuple.Wrapper(name, new ArrayTuple(current));
	}

	public boolean hasNext() {return increment>0 ? current < stop : current > stop;}
	public void stop() {}
	public void remove() {throw new UnsupportedOperationException();}
}
