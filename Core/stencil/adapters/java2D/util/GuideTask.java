package stencil.adapters.java2D.util;

import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Selector;
import stencil.parser.tree.util.Path;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final DisplayCanvas canvas;
	private final Selector selector;
	
	public GuideTask(Guide guideDef, DisplayCanvas canvas) {
		super(new Path(guideDef));
		this.canvas = canvas;
		this.selector = guideDef.getSelector();
	}

	public boolean needsUpdate() {return fragment.getStateQuery().requiresUpdate();}

	public Finisher update() {
		DisplayGuide guide = canvas.getGuide(selector);
		fragment.update(guide);
		return UpdateTask.NO_WORK;
	}
	
	public String toString() {return "Guide update for " + fragment.getSelector().toString();}
}
