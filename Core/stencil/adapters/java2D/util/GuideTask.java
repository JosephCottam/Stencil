
package stencil.adapters.java2D.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import stencil.display.Display;
import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.interpreter.tree.Guide;

/**Update a single guide's data.
 * This is essentially a wrapper for the interpreter's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final String identifier;
	private final Rectangle2D canvasBounds = new Rectangle2D.Double();
	private final AffineTransform viewTransform = new AffineTransform();
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(guideDef, guideDef.identifier());
		this.canvas = canvas;
		this.identifier = guideDef.identifier();
	}

	public boolean needsUpdate() {
		boolean analysisState = super.needsUpdate();
		Rectangle2D bounds = canvas.contentBounds(false);
		AffineTransform viewTrans  = Display.canvas.getComponent().viewTransform();
		if (!canvasBounds.equals(bounds) || !viewTransform.equals(viewTrans)) {
			canvasBounds.setRect(bounds);
			viewTransform.setTransform(viewTrans);
			return true;
		} else {
			return analysisState;
		}
	}
	
	public Finisher update() {
		DisplayGuide guide = canvas.getGuide(identifier);
		viewpointFragment.update(guide, canvas.contentBounds(false), canvas.viewTransform());
		return UpdateTask.NO_WORK;
	}
	
}
