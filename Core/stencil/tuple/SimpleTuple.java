package stencil.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**Tuple that is primarily for positional dereference.
 * 
 * Fields cannot be supplied for this tuple, but get-by-name can be used
 * with indices prefixed by the word 'Value' if needed.
 *  
 * @author jcottam
 *
 */
public class SimpleTuple implements Tuple {
	private static final String PREFIX = "Value";
	
	private final Object[] values;
	
	public SimpleTuple(Collection<Object> values) {this(values.toArray());}
	public SimpleTuple(Object... values) {this.values = values;}
	
	public Object get(String name) throws InvalidNameException {
		try {
			String shortName = name.substring(PREFIX.length());
			int idx = Integer.parseInt(shortName);
			return get(idx);
		}
		catch (Exception e) {throw new RuntimeException("Error determining index for name " + name);}
	}

	public int size() {return values.length;}
	public Object get(int idx) {return values[idx];}

	public List<String> getPrototype() {
		List<String >fields = new ArrayList(values.length);
		for (int i=0; i< values.length; i++) {
			fields.add(PREFIX + i);
		}
		return fields;
	}

	public boolean isDefault(String name, Object value) {return false;}
}
