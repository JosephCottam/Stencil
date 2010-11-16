package stencil.adapters.general;

import java.awt.*;
import java.awt.geom.*;

public final class ScaleWith {
	private ScaleWith() {/**Blank to prevent instances.*/}
	
	/**What can visual scaling be tied to?*/
	public static enum ScaleBy {
		ALL,		/**Scale by both X and Y**/ 
		NONE, 		/**Do not scale with the zoom at all**/
		X, 			/**Scale relative to X only, ignore Y**/
		Y, 			/**Scale relative to Y only, ignore X**/
		LARGEST, 	/**Scale relative to the larger change (X or Y)**/
		SMALLEST	/**Scale relative to the smaller change (X or Y)**/
	}
	
	/**Correct the scaling of a shape based on the view transform passed.
	 * Creates a new shape object and returns it so cached shapes can be safely passed (does not modify any parameters).
	 * 
	 * @param scale Which scaling factor to use
	 * @param shape What shape to scale
	 * @param view Which view to scale with respect to.
	 * @return Zoom-corrected shape.
	 */
	public static Shape scale(final Shape shape, final ScaleBy scale, final AffineTransform view) {
		double factor;
		
		switch (scale) {
			case ALL: return shape;
			case NONE: factor = 1d; break;
			case X: factor = view.getScaleX(); break;
			case Y: factor = view.getScaleY(); break;
			case LARGEST: factor = Math.max(view.getScaleX(), view.getScaleY()); break;
			case SMALLEST: factor =Math.min(view.getScaleX(), view.getScaleY()); break;
			default: throw new Error("Unahndled case in ScaleWith: " + scale.name());

		}
		
		double sx = view.getScaleX() == 0 ? 1 : factor/view.getScaleX();
		double sy = view.getScaleY() == 0 ? 1 : factor/view.getScaleY();

		final AffineTransform trans = AffineTransform.getScaleInstance(sx, sy);
		return trans.createTransformedShape(shape);
	}
}
