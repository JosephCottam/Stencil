package stencil.adapters.java2D.util;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;

import stencil.display.StencilPanel;
import stencil.interpreter.NeedsGuides;
import stencil.interpreter.UpdateGuides;
import stencil.operator.module.ModuleCache;
import stencil.parser.tree.Program;

public class GuideUpdater implements Runnable, Stopable {
	private boolean run = true;
	
	private final NeedsGuides needsGuides;
	private final UpdateGuides updateGuides;
	
	private final Program program;
	private final StencilPanel panel;
	
	public GuideUpdater(Program program, StencilPanel panel) {
		this.program=program;
		this.panel = panel;
		
		NeedsGuides ng = null;
		UpdateGuides ug = null;
		
		ModuleCache c = program.getModuleCache();
		TreeNodeStream treeTokens = new CommonTreeNodeStream(program);

		if (program.getCanvasDef().getGuides().size() >0) {
			ng = new NeedsGuides(treeTokens);
			ug = new UpdateGuides(treeTokens);
			ug.setModuleCache(c);//TODO: Remove when all tuple references are positional
		}
		
		needsGuides = ng;
		updateGuides = ug;
	}
	
	public void run() {
		if (!required()) {return;}
		
		while (run) {
			runOnce();
			panel.repaint();
		}
	}
	
	public synchronized void runOnce() {
		//Refresh the guides (if required)!
		//TODO: Is there a faster way to check needsGuides?--> Collect all guide chain operators in the constructor and just check them instead of a tree traversal every time?
		while (needsGuides != null && needsGuides.check(program)) {
			updateGuides.updateGuides(panel);
		}	
	}
	
	
	public void signalStop() {run = false;}
	
	/**Is this updater required for the program it was passed?
	 * Returns false if there were no guides specified in the program.
	 * @return
	 */
	public boolean required() {return  needsGuides != null;}
}
