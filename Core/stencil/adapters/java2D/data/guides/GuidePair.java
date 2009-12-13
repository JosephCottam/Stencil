package stencil.adapters.java2D.data.guides;

import java.util.Arrays;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

/**Result pair used to create a legend.
 * AutoGuide pairs are converted into these input/output pairs.
 */
final class GuidePair<T> implements Tuple {
	private static final String INPUT_FIELD = "input";
	private static final String RESULT_FIELD = "output";
	private static final String[] FIELDS = new String[]{INPUT_FIELD, RESULT_FIELD};
	 
	public static final int INPUT = Arrays.asList(FIELDS).indexOf(INPUT_FIELD);
	public static final int OUTPUT = Arrays.asList(FIELDS).indexOf(RESULT_FIELD);

	String input;
	T output;
	TuplePrototype prototype;	
	
	public GuidePair(String input, T output) {
		this.input = input;
		this.output = output;
		
		Class[] types = new Class[]{String.class, output.getClass()};	//TODO: Cache the types somehow...otherwise we make this array a LOT
		prototype = new SimplePrototype(FIELDS, types);
	}
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}

	public int size() {return prototype.size();}
	public Object get(int idx) {
		if (idx == INPUT) {return input;}
		if (idx == OUTPUT) {return output;}
		throw new TupleBoundsException(idx, size());
	}

	public TuplePrototype getPrototype() {return prototype;}
	public boolean isDefault(String name, Object value) {return false;}
	public String toString() {return Tuples.toString(this);}

}
