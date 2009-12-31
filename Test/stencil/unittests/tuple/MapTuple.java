package stencil.unittests.tuple;

import stencil.tuple.*;
import stencil.tuple.prototype.TuplePrototype;

import java.util.Map;

/**Tuple wrapper for a map.  Changes made here can be seen
 * in the source map, and changes made to the source map can be seen here.
 * 
 * This class is an incomplete implementation of tuple and should be used
 * for testing purposes only. 
 *    
 * @author jcottam
 *
 */
public class MapTuple implements MutableTuple {
	protected Map<String, Object> map;

	public MapTuple(Map<String, Object> source) {this.map = source;}

	public Object get(String name) throws InvalidNameException {return map.get(name);}

	public boolean hasField(String name) {return map.containsKey(name);}

	public boolean isDefault(String name, Object value) {return false;}

	public Object get(int idx) throws TupleBoundsException {
		throw new RuntimeException("Cannot positionally de-reference...");
	}

	public TuplePrototype getPrototype() {
		throw new RuntimeException("Prototype not available for map tuple...");
	}

	public int size() {return map.size();}

	public void set(String field, Object value) {
		map.put(field, value);		
	}
};
