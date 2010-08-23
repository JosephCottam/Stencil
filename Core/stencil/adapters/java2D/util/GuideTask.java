package stencil.adapters.java2D.util;

import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Selector;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final Selector selector;
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(guideDef, guideDef.getStateQuery(), guideDef.getSelector().toString());
		this.canvas = canvas;
		this.selector = guideDef.getSelector();
	}

	public Finisher update() {
		DisplayGuide guide = canvas.getGuide(selector);
		viewPointFragment.update(guide);
		return UpdateTask.NO_WORK;
	}
	
}
