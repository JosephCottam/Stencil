package stencil.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**Tuple backed by a map.  
 * Updates to the backing map WILL be represented in the tuple.
 * 
 * If an immutable map is passed, the set method will throw an UnsupportedOperationException.
 * 
 * @author jcottam
 *
 */
public class MapTuple implements MutableTuple {
	protected Map<String, Object> map;
	
	public MapTuple(Map<String, Object> source) {this.map = source;}
	
	public Object get(String name) throws InvalidNameException {return map.get(name);}
	public Object get(int idx) {
		int index = idx;
		for (String key: map.keySet()) {
			if (index==0) {return get(key);}
			index--;
		}
		throw new TupleBoundsException(idx, size());
	}
	public int size() {return map.size();}
	
	public void set(String key, Object value) {
		try{map.put(key, value);}
		catch (UnsupportedOperationException e) {throw new UnsupportedOperationException("Cannot modify map tuple with immutable backing map.");}
	}
	
	public List<String> getPrototype() {return new ArrayList(map.keySet());}
	
	public String toString() {return Tuples.toString(this);}
	
	public boolean isDefault(String name, Object value) {return false;}
}