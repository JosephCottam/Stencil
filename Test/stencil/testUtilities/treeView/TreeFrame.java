package stencil.testUtilities.treeView;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.Parser;

/**Provides a view of an ANTLR Tree as a JTree wrapped in a JFrame.
 *
 * The ANTLR Tree is assumed to be unchanging after it is initially displayed.
 *
 * @author jcottam
 */
public class TreeFrame extends JFrame {
	private static final long serialVersionUID = 3820466049034765300L;

	/**Construct a TreeFrame
	 *
	 * @param tree  Sources tree to represent in the resulting tree view
	 * @param typeToName Class that can be used to map ANTLR type proxies to type names;
	 *                   the Parser that generated the tree is one such mapping, but any
	 *                   class with field values that match the type proxies can be used.
	 *                   The name of the field will be used to annotate the text of the tree node.
	 */
	public TreeFrame(Tree tree, Parser parser) {
		super();
		
		JTree t = new JTree();
		ANTLRNode root = new ANTLRNode(tree, null, parser);
		DefaultTreeModel model = new DefaultTreeModel(root);		
		t.setModel(model);
		
		this.setContentPane(new JScrollPane(t));
		this.setSize(300, 600);
	}

}
