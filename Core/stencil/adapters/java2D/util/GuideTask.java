package stencil.adapters.java2D.util;

import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.interpreter.tree.Guide;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final String identifier;
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(guideDef, guideDef.identifier());
		this.canvas = canvas;
		this.identifier = guideDef.identifier();
	}

	public Finisher update() {
		DisplayGuide guide = canvas.getGuide(identifier);
		viewpointFragment.update(guide, canvas.getContentBounds(false));		//HACK: This 'getContentBounds' is a source of non-determinism in the guide system
		return UpdateTask.NO_WORK;
	}
	
}
