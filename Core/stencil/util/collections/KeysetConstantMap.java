package stencil.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**Map that always returns the same value, but still performs
 * a key check to verify that the requested key has been entered.
 **/
public final class KeysetConstantMap<K,V> extends ConstantMap<K,V> {
	private final Set<K> keyset = new HashSet();
	
	protected final class Entry implements java.util.Map.Entry<K, V> {
		private final K key;
		public Entry(K key) {this.key = key;}
		public K getKey() {return key;}
		public V getValue() {return value;}
		public V setValue(V value) {throw new UnsupportedOperationException();}
	}
	
	public KeysetConstantMap(V value) {super(value);}
	public KeysetConstantMap(Collection<K> keys, V value) {
		super(value);
		keyset.addAll(keys);
	}
	
	public V get(Object key) {
		if (keyset.contains(key)) {return value;}
		return null; 
	}
	
	public boolean containsKey(Object key) {return keyset.contains(key);}

	public Set<java.util.Map.Entry<K,V>> entrySet(){
		Set<java.util.Map.Entry<K, V>> entries = new HashSet();
		for (K key: keyset) {entries.add(new Entry(key));}
		return entries;
	}
	
	public Set<K> keySet() {return keyset;}

	public V put(K key, V value) {
		if (keyset.contains(key)) {return value;}
		else {keyset.add(key); return null;}
	}
	
	public V remove(Object key) {
		if (keyset.contains(key)) {
			keyset.remove(key);
			return value;
		}
		return null;
	}
	public int size() {return keyset.size();}
	public Collection<V> values() {return null;}

}
