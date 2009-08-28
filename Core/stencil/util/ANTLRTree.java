package stencil.util;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**Utilities for working with ANTLR Trees.*/
public abstract class ANTLRTree {
	/**Indicate that no node has been found with the requested text value.*/
	public static final class NameNotFoundException extends IllegalArgumentException {
		public NameNotFoundException(String name) {
			super("No child with value " + name + " found.");
		}
	}
	
	/**Look through all children of the root, return the node whose text matches the key field.
	 * @param root Node whose children should be searched
	 * @param key Value to look for as the text field
	 * @return
	 * @throws NameNotFoundException  Key is not encountered as the text of any child.
	 */
	public static final Tree search(Tree root, String key) throws NameNotFoundException {
		key = key.toUpperCase();
		for (CommonTree child: (List<CommonTree>) ((CommonTree) root).getChildren()) {
			if (child.getText().toUpperCase().equals(key)) {return child;}
		}
		throw new NameNotFoundException(key);
	}
	
}
