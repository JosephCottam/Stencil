package stencil.adapters.java2D.util;

import stencil.display.StencilPanel;
import stencil.parser.tree.Guide;
import stencil.parser.tree.util.Path;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask<Guide> {
	private final StencilPanel panel;

	public GuideTask(Guide guideDef, StencilPanel panel) {
		super(new Path(guideDef));
		this.panel = panel;
	}

	public boolean needsUpdate() {return fragment.getStateQuery().requiresUpdate();}

	public Finisher update() {
		fragment.update(panel);
		return UpdateTask.NO_WORK;
	}
	
	public String toString() {return "Guide update for " + fragment.getSelector().toString();}
}
