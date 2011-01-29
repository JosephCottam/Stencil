package stencil.adapters.java2D.util;

import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Selector;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final Selector selector;
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(guideDef, guideDef.stateQuery(), guideDef.selector().toString());
		this.canvas = canvas;
		this.selector = guideDef.selector();
	}

	public Finisher update() {
		DisplayGuide guide = canvas.getGuide(selector);
		viewPointFragment.update(guide);
		return UpdateTask.NO_WORK;
	}
	
}
