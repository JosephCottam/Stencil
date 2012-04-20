package stencil.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**Map that returns a constant value and doesn't remember its keys...
 * Not really a map, but its useful for providing a default value for certain
 * map variables.
 **/
public class ConstantMap<K,V> implements Map<K,V> {
	protected V value;
	
	public ConstantMap(V value) {this.value = value;}
	
	@Override
	public V get(Object key) {return value;}
	@Override
	public void clear() {throw new UnsupportedOperationException();}
	@Override
	public boolean containsKey(Object key) {return true;}
	@Override
	public boolean containsValue(Object value) {return this.value.equals(value);}
	@Override
	public Set<java.util.Map.Entry<K,V>> entrySet() {return null;}
	@Override
	public boolean isEmpty() {return false;}
	@Override
	public Set<K> keySet() {return null;}
	@Override
	public V put(K key, V value) {return null;}
	@Override
	public void putAll(Map<? extends K, ? extends V> t) {throw new UnsupportedOperationException();}
	@Override
	public V remove(Object key) {return null;}
	@Override
	public int size() {return 1;}
	@Override
	public Collection<V> values() {return Arrays.asList(value);}

}
