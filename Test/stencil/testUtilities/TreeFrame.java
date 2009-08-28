package stencil.testUtilities;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

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

	public static String NULL_STRING = "<null>";
	public static String JOIN_STRING  = ": ";
	
	public static boolean showClass = true;

	/**Utility object to convert between a list and an enumeration.
	 * @author jcottam
	 *
	 */
	private static class NodeEnumeration implements Enumeration<AntlrNode> {
		private List<AntlrNode> source;
		private int next=0;

		public NodeEnumeration(List<AntlrNode> source) {this.source = source;}

		public boolean hasMoreElements() {return next < source.size();}

		public AntlrNode nextElement() {
			AntlrNode v = source.get(next);
			next++;
			return v;
		}
	}

	/**Wrapper class for ANTLR nodes to bridge to swing TreeModel.
	 *
	 * Child TreeNodes are created as needed, wrapping the children
	 * of the root element.
	 *
	 * @author jcottam
	 *
	 */
	public static class AntlrNode implements TreeNode {
		private Tree root;
		private AntlrNode parent;
		private List<AntlrNode> children;
		private Parser parser;

		public AntlrNode(Tree root, AntlrNode parent, Parser parser) {
			this.root = root;
			this.parent = parent;
			this.parser = parser;
		}

		public Enumeration<AntlrNode> children() {
			ensureChildren();
			return new NodeEnumeration(this.children);
		}

		public boolean getAllowsChildren() {return root.getChildCount() > 0;}

		public TreeNode getChildAt(int childIndex) {
			ensureChildren();
			return children.get(childIndex);
		}

		public int getChildCount() {return root.getChildCount();}

		public int getIndex(TreeNode node) {
			ensureChildren();
			return children.indexOf(node);
		}

		public TreeNode getParent() {return parent;}
		public boolean isLeaf() {return !getAllowsChildren();}

		/**The default tree cell renderer uses the toString of the
		 * TreeNode as the display; this method passes that through
		 * to the toString of the backing ANTLR Tree.
		 */
		public String toString() {
			StringBuilder b = new StringBuilder();

			String type = typeName(root.getType());
			String text = root.getText();
			String join = JOIN_STRING;


			if (text == null) {text = NULL_STRING;}
			if (type.equals(text)) {type = "";}	//The default text is the type name, no point in printing it twice
			if (type.equals("") || text.equals("")) {join = "";}

			b.append(type);
			b.append(join);
			b.append(text);

			if (showClass) {
				b.append("	(");
				b.append(root.getClass().getName());
				b.append(")");
			}
			
			return b.toString();
		}

		private String typeName(int type) {
			if (parser==null) {return "";}

			try {
				for (Field f:parser.getClass().getFields()) {
					if (!f.getType().isAssignableFrom(int.class)) {continue;}
					if (f.getInt(parser) == type) {return f.getName();}
				}
			} catch (Exception e) {return "";}
			return "";
		}

		/**Populate the TreeNode list of children, if not already populated.*/
		private void ensureChildren() {
			if (children ==null) {
				AntlrNode[] newChildren = new AntlrNode[root.getChildCount()];

				for (int i=0; i< newChildren.length; i++) {
					newChildren[i] = new AntlrNode(root.getChild(i), this, parser);
				}
				children = Arrays.asList(newChildren);
			}
		}

	}

	/**Construct a swing TreeModel for the given ANTLR tree.
	 * This tree model be used in any JTree.*/

	public static TreeModel makeModel(Tree source, Parser parser) {
		AntlrNode root = new AntlrNode(source, null, parser);

		DefaultTreeModel model = new DefaultTreeModel(root);
		return model;
	}

	/**Construct a TreeFrame for the given tree, no name mapping*/
	public TreeFrame(Tree tree) {this(tree, null);}

	/**Construct a TreeFrame
	 *
	 * @param tree  Sources tree to represent in the resulting tree view
	 * @param typeToName Class that can be used to map ANTLR type proxies to type names;
	 * 					the Parser that generated the tree is one such mapping, but any
	 * 					class with field values that match the type proxies can be used.
	 * 					The name of the field will be used to annotate the text of the tree node.
	 */
	public TreeFrame(Tree tree, Parser parser) {
		JTree t = new JTree();
		t.setModel(makeModel(tree, parser));
		this.setContentPane(new JScrollPane(t));
		this.setSize(150, 300);
	}

}
