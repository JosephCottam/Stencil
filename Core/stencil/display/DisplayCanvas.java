package stencil.display;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.JComponent;

import stencil.adapters.java2D.data.Guide2D;
import stencil.interpreter.tree.Selector;

/**What should a canvas component be able to do?*/
public abstract class DisplayCanvas extends JComponent {
	/**Instance lock.  Used to prevent pre-paint/analysis concurrency issues.
	 * If required concurrently with other locks, locking order is render then vis.
	 **/
	public final Object visLock = new Object(); 
	
	/**Instance lock.  Used to prevent multiple renderings (including the pre-render tasks) from concurrently running.
	 * If required concurrently with other locks, locking order is render then vis.
	 */
	public final Object renderLock = new Object(); 
	
	public abstract DisplayGuide getGuide(Selector sel);
	public abstract void addGuide(Selector sel, Guide2D guide);
	public abstract boolean hasGuide(Selector sel);
	public abstract Collection<Guide2D> getGuides();
	
	/**Get the bounds of all entities on this canvas.
	 * TODO: How should this address view-guides
	 * 
	 * @param includeGuides  Should this include canvas-level guides?
	 * @return
	 */
	public abstract Rectangle2D getContentBounds(boolean includeGuides);
}
