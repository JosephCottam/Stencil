package stencil.adapters.java2D.util;

import stencil.display.StencilPanel;
import stencil.parser.tree.Guide;

/**Update a single guide's data.
 * This is essentially a wrapper for the StencilTree's GuideDef.
 **/
public class GuideTask extends UpdateTask {	
	private final StencilPanel panel;
	private final Guide guideDef;

	public GuideTask(Guide guideDef, StencilPanel panel) {
		this.panel = panel;
		this.guideDef = guideDef;
	}

	public boolean needsUpdate() {return guideDef.getStateQuery().requiresUpdate();}

	public void update() {guideDef.update(panel);}
	
	public String toString() {return "Guide update for " + guideDef.getSelector().toString();}
}
