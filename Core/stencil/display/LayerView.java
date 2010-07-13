package stencil.display;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;


/**Access to the consistent state of a layer.
 * The precise definition of consistency is left up to the
 * layer.  Views (and iterators returned from them) MAY throw
 * ConcurrentModificationException if inconsistencies are detected,
 * but this is not a guarantee.
 *
 * @param <T> Type of the things being stored in the backing layer.
 */
public interface LayerView<T extends Glyph> extends Iterable<T>{
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
}
