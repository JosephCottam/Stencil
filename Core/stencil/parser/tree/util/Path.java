package stencil.parser.tree.util;

import stencil.parser.tree.StencilTree;
import java.util.*;

/**A path is the way to navigate from the tree root to a specific node.
 * Applying a path to a root will give the original node if it is applied
 * to the tree the path was originally created on OR the corresponding node
 * if it is applied to a corresponding tree.
 */
public final class Path {
	final List<Integer> turns;
	
	public Path(StencilTree target) {
		List turns = new LinkedList();
		
		while (target != null) {
			turns.add(0, target.getChildIndex());
			target = target.getParent();
		}
		this.turns = Collections.unmodifiableList(turns);
	}
	
	public StencilTree apply(StencilTree root) {
		List<Integer> turns = new ArrayList(this.turns);
		while (turns.size() >0) {
			root = (StencilTree) root.getChild(turns.get(0));
			turns.remove(0);
		}
		return root;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Path)) {return false;}
		Path op = (Path) other;
		if (op.turns.size() != turns.size()) {return false;}
		for (int i=0; i< turns.size(); i++) {
			if (!op.turns.get(i).equals(turns.get(i))) {return false;}
		}
		return true;
	}
	
	public int hashCode() {
		int code=1;
		for (int i=0; i<turns.size(); i++) {
			code = (turns.get(i) ^ code);
		}
		return code;
	}
}
