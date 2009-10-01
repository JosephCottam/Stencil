package stencil.adapters.java2D.data;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import stencil.display.DisplayGuide;
import stencil.util.AutoguidePair;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public interface Guide2D extends DisplayGuide {
	
	/**Given a set of autoguide pairs, create an appropraite axis representation.
	 * 
	 * @param elements
	 */
	public void setElements(List<AutoguidePair> elements);
	
	/**Render the guide to the given graphics object.  
	 * After rendering, the graphics object transform should
	 * be the same as the passed transform.
	 * 
	 * @param g Graphics to render on
	 * @param viewTransform Required value of the graphics transform at method termination
	 */
	public void render(Graphics2D g, AffineTransform viewTransform);

}
