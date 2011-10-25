package stencil.display;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.JComponent;


/**What should a canvas component be able to do?*/
public abstract class DisplayCanvas extends JComponent {
	/**Instance lock.  Used to prevent pre-paint/analysis concurrency issues.
	 * If required concurrently with other locks, locking order is render then vis.
	 **/
	public final Object visLock = new Object(); 
	
	/**Instance lock.  Used to prevent ensure that tableView grabs capture all layers in a consistent state
	 * (i.e., no layer gets merged after some layers have been grabbed).
	 * If required concurrently with visLock, locking order is vis then tableCapture.
	 */
	public final Object tableCaptureLock = new Object(); 
	
	public abstract DisplayGuide getGuide(String identifier);
	public abstract void addGuide(Guide2D guide);
	public abstract boolean hasGuide(String identifier);
	public abstract Collection<Guide2D> getGuides();
	
	/**Get the bounds of all entities on this canvas.
	 * 
	 * @param includeGuides  Should this include canvas-level guides?
	 * @return
	 */
	public abstract Rectangle2D contentBounds(boolean includeGuides);


	public abstract AffineTransform viewTransform();

}
