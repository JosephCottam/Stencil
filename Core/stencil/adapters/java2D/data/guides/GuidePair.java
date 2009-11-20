package stencil.adapters.java2D.data.guides;

import java.util.Arrays;
import java.util.List;

import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.util.Tuples;

/**Result pair used to create a legend.
 * AutoGuide pairs are converted into these input/output pairs.
 */
final class GuidePair<T> implements Tuple {
	private static final List<String> FIELDS = Arrays.asList("INPUT", "OUTPUT");
	String input;
	T output;
	
	public GuidePair(String input, T output) {
		this.input = input;
		this.output = output;
	}
	public Object get(String name) throws InvalidNameException {
		if (name.equals("INPUT")) {return input;}
		if (name.equals("OUTPUT")) {return output;}
		throw new InvalidNameException(name, FIELDS);
	}
	
	public List<String> getPrototype() {return FIELDS;}
	public boolean hasField(String name) {return getPrototype().contains(name);}
	public boolean isDefault(String name, Object value) {return false;}
	public String toString() {return Tuples.toString(this);}

}
