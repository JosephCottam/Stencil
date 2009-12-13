package stencil.util;

import java.util.Arrays; 

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

//final because it is immutable
public final class AutoguidePair<I,R> implements Tuple {
	private static final String INPUT_FIELD = "input";
	private static final String RESULT_FIELD = "result";
	private static final String[] FIELDS = new String[]{INPUT_FIELD, RESULT_FIELD};
	public static final int INPUT = Arrays.asList(FIELDS).indexOf(INPUT_FIELD);
	public static final int RESULT = Arrays.asList(FIELDS).indexOf(RESULT_FIELD);
	
	private I[] input;
	private R[] result;
	private TuplePrototype prototype;
	
	public AutoguidePair(I[] input, R[] result) {
		this.input = input;
		this.result = result;
		
		prototype = new SimplePrototype(FIELDS, new Class[]{input.getClass(), result.getClass()});	//TODO: Cache this somehow because it will be created a lot...
	}
	
	public I[] getInput() {return input;}
	public R[] getResult() {return result;}
	
	public String toString() {
		return Arrays.deepToString(input) + ":" + Arrays.deepToString(result);
	}

	public Object get(String name) {return Tuples.namedDereference(name, this);}

	public int size() {return FIELDS.length;}
	public Object get(int idx) {
		if (idx == INPUT) {return input;}
		if (idx == RESULT) {return result;}
		throw new TupleBoundsException(idx, size());
	}
	
	public TuplePrototype getPrototype() {return prototype;}
	
	public boolean isDefault(String name, Object value) {return false;}
}
