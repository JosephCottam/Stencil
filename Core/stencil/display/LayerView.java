package stencil.display;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import stencil.tuple.Tuple;


/**Access to the consistent state of a layer.
 * The precise definition of consistency is left up to the
 * layer.  Views (and iterators returned from them) MAY throw
 * ConcurrentModificationException if inconsistencies are detected,
 * but this is not a guarantee.
 *
 * @param <T> Type of the things being stored in the backing layer.
 */
public interface LayerView<T extends Glyph> extends Iterable<T>{
	/**Entry into a dynamic binding.
	 * TODO: Factor this out somehow, it is an ugly implementation leak...A good idea would be to parameterize getSourceData by teh groupID and return a filtering collection of some sort... 
	 */
	public static final class DynamicEntry {
		public int groupID;
		public Tuple t;
		public DynamicEntry(int groupID, Tuple t) {
			this.groupID = groupID;
			this.t = t;
		}
 	}
	
	
	
	/**Return an iterator of the tuples of this layer*/
	public Iterator<T> iterator();

	/**Return an collection of the tuples of this layer 
	 * whose iterator will be in the order the glyphs should be rendered.*/
	public Collection<T> renderOrder();
	
	/**Get the bounding rectangle.  The rectangle handed back
	 * may be the internally stored bounds as the receiver promises
	 * to "play nice" with it (e.g. not modify the received rectangle).
	 * @return
	 */
	public Rectangle getBoundsReference();
	
	/**What is the name of the corresponding layer.*/
	public String getLayerName();
	
	/**What is the projected size of the view (e.g. how many tuples will the iterator return)?*/
	public int size();
	
	/**Get the stateID That corresponds to this view.*/
	public int getStateID();
	
	public T find(String id);
	
	/**Get the source data for dynamic bindings related to this layer.*/
	public Map<String, DynamicEntry> getSourceData();
}
