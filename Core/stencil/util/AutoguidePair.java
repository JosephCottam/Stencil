package stencil.util;

import java.util.Arrays; 
import java.util.List;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;

//final because it is immutable
public final class AutoguidePair<I,R> implements Tuple {
	private static final String INPUT_FIELD = "input";
	private static final String RESULT_FIELD = "result";
	private static final List<String> FIELDS = Arrays.asList(INPUT_FIELD, RESULT_FIELD);
	public static final int INPUT = FIELDS.indexOf(INPUT_FIELD);
	public static final int RESULT = FIELDS.indexOf(RESULT_FIELD);
	
	private I[] input;
	private R[] result;
	
	public AutoguidePair(I[] input, R[] result) {
		this.input = input;
		this.result = result;
	}
	
	public I[] getInput() {return input;}
	public R[] getResult() {return result;}
	
	public String toString() {
		return Arrays.deepToString(input) + ":" + Arrays.deepToString(result);
	}

	public Object get(String name) {return Tuples.namedDereference(name, this);}

	public int size() {return FIELDS.size();}
	public Object get(int idx) {
		if (idx == INPUT) {return input;}
		if (idx == RESULT) {return result;}
		throw new TupleBoundsException(idx, size());
	}
	
	public List<String> getPrototype() {return FIELDS;}
	
	public boolean isDefault(String name, Object value) {return false;}
}
