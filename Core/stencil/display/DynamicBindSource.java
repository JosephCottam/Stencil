package stencil.display;

import java.util.Map;

import stencil.tuple.Tuple;


/**Access to the dynamic binding consistent source data of a layer.
 *
 * @param <T> Type of the things being stored in the backing layer.
 */
public interface DynamicBindSource<T extends Glyph> extends Iterable<T> {
	/**Get the source data for dynamic bindings related to this layer.*/
	public Map<String, Tuple> getSourceData();
	
	/**Size of the underlying data set.*/
	public int size();
}
