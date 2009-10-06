package stencil.adapters.java2D.util;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;

import stencil.display.StencilPanel;
import stencil.interpreter.NeedsGuides;
import stencil.interpreter.UpdateGuides;
import stencil.operator.module.ModuleCache;
import stencil.parser.tree.Layer;
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

		for (Layer l: program.getLayers()) {
			if (l.getGuides().size() >0) {				
				ng = new NeedsGuides(treeTokens);
				ug = new UpdateGuides(treeTokens);
				ug.setModuleCache(c);//TODO: Remove when all tuple references are positional
				break;	
			}
		}
		
		needsGuides = ng;
		updateGuides = ug;
	}
	
	public void run() {
		if (needsGuides == null) {return;}
		
		while (run) {
			//Refresh the guides (if required)!
			//TODO: Is there a faster way to check needsGuides?--> Collect all guide chain operators in the constructor and just check them instead of a tree traversal every time?
			while (needsGuides != null && needsGuides.check(program)) {
				updateGuides.updateGuides(panel);
			}
			panel.repaint();
		}
	}
	
	public void signalStop() {run = true;}
}
