package stencil.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**Map that returns a constant value and doesn't remember its keys...
 * Not really a map, but its useful for providing a default value for certain
 * map variables.
 **/
public class ConstantMap<K,V> implements Map<K,V> {
	private V value;
	
	public ConstantMap(V value) {this.value = value;}
	
	public V get(Object key) {return value;}
	public void clear() {throw new UnsupportedOperationException();}
	public boolean containsKey(Object key) {return true;}
	public boolean containsValue(Object value) {return false;}
	public Set<java.util.Map.Entry<K,V>> entrySet() {return null;}
	public boolean isEmpty() {return false;}
	public Set<K> keySet() {return null;}
	public V put(K key, V value) {return null;}
	public void putAll(Map<? extends K, ? extends V> t) {throw new UnsupportedOperationException();}
	public V remove(Object key) {return null;}
	public int size() {return 0;}
	public Collection<V> values() {return null;}

}
