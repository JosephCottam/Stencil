package stencil.testUtilities.treeView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.antlr.runtime.Parser;
import org.antlr.runtime.tree.Tree;

/**Wrapper class for ANTLR nodes to bridge to swing TreeModel.
 *
 * Child TreeNodes are created as needed, wrapping the children
 * of the root element.
 *
 * @author jcottam
 *
 */
public class ANTLRNode implements TreeNode {
	/**Utility object to convert between a list and an enumeration.
	 * @author jcottam
	 *
	 */
	protected static class NodeEnumeration implements Enumeration<ANTLRNode> {
		private List<ANTLRNode> source;
		private int next=0;

		public NodeEnumeration(List<ANTLRNode> source) {this.source = source;}

		public boolean hasMoreElements() {return next < source.size();}

		public ANTLRNode nextElement() {
			ANTLRNode v = source.get(next);
			next++;
			return v;
		}
	}

	/**Should the 'toString' include class information?*/
	public static boolean showClass = true;
	
	/**What should be printed when a null value is encountered?*/
	public static String nullString = "<null>";
	
	/**What separates a toke type from the token string?*/
	public static String joinString  = ": ";

	
	protected Tree root;
	protected ANTLRNode parent;
	protected Parser parser;
	private List<ANTLRNode> children;
	
	public ANTLRNode(Tree root, ANTLRNode parent, Parser parser) {
		this.root = root;
		this.parent = parent;
		this.parser = parser;
	}

	public Enumeration<ANTLRNode> children() {
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
			ANTLRNode[] newChildren = new ANTLRNode[root.getChildCount()];

			for (int i=0; i< newChildren.length; i++) {
				newChildren[i] = childNode(root.getChild(i));
			}
			children = Arrays.asList(newChildren);
		}
	}
	
	
	/**Method used to create child nodes.
	 * 
	 * This method may be used with subclasses that have the
	 * same three-argument constructor as this root node.
	 * This means that if only toString is modified, no change
	 * is required here.  If a different constructor is
	 * required, then this method must be overridden as well.
	 *   
	 * @param child
	 * @return
	 */
	public ANTLRNode childNode(Tree child) {
		Class[] parameterTypes = {Tree.class, ANTLRNode.class, Parser.class};
		ANTLRNode childNode;
		try {
			Constructor c = getClass().getConstructor(parameterTypes);
			childNode = (ANTLRNode) c.newInstance(child, this, parser);
		} catch (Exception e) {throw new RuntimeException(e);}
		return childNode;
	}
	
	/**The default tree cell renderer uses the toString of the
	 * TreeNode as the display; this method reports common information
	 * of ANTLR nodes.
	 * 
	 * If the 'showClass' static class variable is set to true, the class
	 * name will be included.
	 * .  
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();

		String type = typeName(root.getType());
		String text = root.toString();
		String join = joinString;


		if (text == null) {text = nullString;}
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

}