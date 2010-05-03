package stencil.adapters.java2D.util;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;

import stencil.display.StencilPanel;
import stencil.interpreter.NeedsGuides;
import stencil.interpreter.UpdateGuides;
import stencil.parser.tree.Program;

//TODO: Split this up so it is a task per-guide requested (they are independent, so they can be parallelized)
public class GuideTask implements UpdateTask {	
	private final NeedsGuides needsGuides;
	private final UpdateGuides updateGuides;
	
	private final Program program;
	private final StencilPanel panel;
	
	public GuideTask(Program program, StencilPanel panel) {
		this.program=program;
		this.panel = panel;
		
		NeedsGuides ng = null;
		UpdateGuides ug = null;
		
		TreeNodeStream treeTokens = new CommonTreeNodeStream(program);

		if (program.getCanvasDef().getGuides().size() >0) {
			ng = new NeedsGuides(treeTokens);
			ug = new UpdateGuides(treeTokens);
		}
		
		needsGuides = ng;
		updateGuides = ug;
	}
	
	/**Is this updater required for the program it was passed?
	 * Returns false if there were no guides specified in the program.
	 * @return
	 */
	public boolean required() {return  needsGuides != null;}

	public void conservativeUpdate() {if (needsUpdate()) {update();}}

	//TODO: Is there a faster way to check needsGuides?--> Collect all guide chain operators in the constructor and just check them instead of a tree traversal every time?
	public boolean needsUpdate() {return needsGuides != null && needsGuides.check(program);}

	public void update() {updateGuides.updateGuides(panel);}
}
