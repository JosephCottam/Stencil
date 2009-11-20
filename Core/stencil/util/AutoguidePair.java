package stencil.util;

import java.util.Arrays; 
import java.util.List;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;

//final because it is immutable
public final class AutoguidePair<I,R> implements Tuple {
	List<String> FIELDS = Arrays.asList("input", "result");
	
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

	public Object get(String name) throws InvalidNameException {
		if (name.equals("input")) {return input;}
		if (name.equals("result")) {return result;}
		throw new InvalidNameException(name);
	}

	public List<String> getPrototype() {return FIELDS;}
	public boolean hasField(String name) {return FIELDS.contains(name);}
	public boolean isDefault(String name, Object value) {return false;}
}
