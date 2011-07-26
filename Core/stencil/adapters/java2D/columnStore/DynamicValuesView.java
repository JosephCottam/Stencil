package stencil.adapters.java2D.columnStore;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static stencil.parser.ParserConstants.DYNAMIC_STORE_FIELD;


/**Provide access to a table view as-if it were a map from id to dynamic value inputs.**/
final class DynamicValuesView implements Map {
	private final TableShare source;
	private final int dynamicColumn;
	
	public DynamicValuesView(TableShare source) {
		this.source = source;
		dynamicColumn = source.source.tenured().schema().indexOf(DYNAMIC_STORE_FIELD);
	}
	
	@Override
	public Set keySet() {return source.index.keySet();}


	@Override
	public boolean containsKey(Object key) {return source.index.containsKey(key);}

	@Override
	public Set entrySet() {throw new UnsupportedOperationException();}

	@Override
	public Object get(Object key) {return source.columns[dynamicColumn].get(source.index.get(key));}

	@Override
	public boolean isEmpty() {return source.index.isEmpty();}

	@Override
	public int size() {return source.index.size();}

	@Override
	public Collection values() {throw new UnsupportedOperationException();}

	@Override
	public boolean containsValue(Object value) {throw new UnsupportedOperationException();}
	
	@Override
	public void clear() {throw new UnsupportedOperationException();}

	@Override
	public Object put(Object key, Object value) {throw new UnsupportedOperationException();}

	@Override
	public void putAll(Map m) {throw new UnsupportedOperationException();}

	@Override
	public Object remove(Object key) {throw new UnsupportedOperationException();}
}
