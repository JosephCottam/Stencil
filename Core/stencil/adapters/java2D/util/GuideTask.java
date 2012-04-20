
package stencil.adapters.java2D.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import stencil.display.Display;
import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.display.DisplayLayer;
import stencil.interpreter.tree.Guide;

/**Update a single guide's data.
 * This is essentially a wrapper for the interpreter's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final String identifier;
	private final Rectangle2D canvasBounds = new Rectangle2D.Double(); //TODO: Replace with associated layer bounds
	private final AffineTransform viewTransform = new AffineTransform();
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(guideDef, guideDef.identifier());
		this.canvas = canvas;
		this.identifier = guideDef.identifier();
	}

	@Override
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
	
	@Override
	public Finisher update() {
		DisplayLayer layer = layerFor(identifier);
		
		DisplayGuide guide = canvas.getGuide(identifier);
		viewpointFragment.update(guide, layer.bounds().basis(), canvas.viewTransform());
		return UpdateTask.NO_WORK;
	}
	
	private DisplayLayer layerFor(String id) {
		String layerId = id.substring(0, id.indexOf(" "));
		DisplayLayer[] layers = ((stencil.adapters.java2D.Canvas) canvas).layers;
		for (DisplayLayer layer:layers) {
			if (layer.name().equals(layerId)) {return layer;}
		}
		throw new Error();
	}
	
}
