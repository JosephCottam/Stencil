package stencil.adapters.java2D.data.guides;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;

/**Result pair used to create a legend.
 * AutoGuide pairs are converted into these input/output pairs.
 */
final class GuidePair<T> implements Tuple {
	private static final String INPUT_FIELD = "input";
	private static final String RESULT_FIELD = "output";
	private static final List<String> PROTOTYPE = Arrays.asList(INPUT_FIELD, RESULT_FIELD);
	public static final int INPUT = PROTOTYPE.indexOf(INPUT_FIELD);
	public static final int OUTPUT = PROTOTYPE.indexOf(RESULT_FIELD);

	String input;
	T output;
	
	public GuidePair(String input, T output) {
		this.input = input;
		this.output = output;
	}
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}

	public int size() {return PROTOTYPE.size();}
	public Object get(int idx) {
		if (idx == INPUT) {return input;}
		if (idx == OUTPUT) {return output;}
		throw new TupleBoundsException(idx, size());
	}

	public List<String> getPrototype() {return PROTOTYPE;}
	public boolean isDefault(String name, Object value) {return false;}
	public String toString() {return Tuples.toString(this);}

}
