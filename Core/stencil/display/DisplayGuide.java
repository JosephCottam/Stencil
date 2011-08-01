package stencil.display;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import stencil.tuple.PrototypedTuple;

/**Conforming to this interface is required for guides used as automatic generation targets.
 * 
 * Additionally, if a custom default specializer is desired, a public static field DEFAULT_ARGUMENTS
 * should be defined (otherwise, a zero-argument list will be used).
 */
public interface DisplayGuide {
	/**Prepare a guide for rendering.
	 * @param elements The list of input/result pairs
	 * @param parentBounds The bounds of the entity this guide is associated  with (bounds should not guides) 
	 */
	public void setElements(List<PrototypedTuple> elements, Rectangle2D parentBounds, AffineTransform viewTransform);
}
