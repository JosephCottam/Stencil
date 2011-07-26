package stencil.adapters.java2D.render.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import stencil.display.DisplayGuide;
import stencil.interpreter.tree.Guide;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public abstract class Guide2D implements DisplayGuide {
	protected final String identifier;
	protected final Guide guideDef;
	protected Guide2D(Guide def) {
		identifier = def.identifier();
		this.guideDef = def;
	}
	
	/**What is this guide for?*/
	public String getIdentifier() {return identifier;}
	
	
	/**Get the bounding box of the glyph.  The bounding box returned from
	 * this method MAY BE (but is not necessarily) a direct pointer to an
	 * internally maintained bounding box.  It is generally not safe to modify
	 * the value returned by this method.
	 *  
	 * @return
	 */
	public abstract Rectangle2D getBoundsReference();
	
	/**Render the guide to the given graphics object.  
	 * After rendering, the graphics object transform should
	 * be the same as the passed transform.
	 * 
	 * @param g Graphics to render on
	 * @param viewTransform Required value of the graphics transform at method termination
	 */
	public abstract void render(Graphics2D g, AffineTransform viewTransform);
}
