package stencil.operator.module.provided.layouts;

import java.util.List;
import java.util.ArrayList;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;


/**An attempt to implement the positioning algorithm from
 * 
 * John Q. Walker II, "A Node-positioning algorithm for general trees."
 * Software-Practice and Experience, John Wiley & Sons Ltd., 1990.
 * 
 * @author jcottam
 *
 */
public class TreeLayout {
	private static final float LEVEL_SEP = 10;
	private static final float SIBLING_SEP = 10;
	private static final float SUBTREE_SEP = 50;	
	
	private static final String[] NAMES = new String[]{"X","Y"};
	
	private static final class Tree {
		public final String id;
		public final List<Tree> children = new ArrayList();
		public final Tree parent;
		public float x;
		public float y;
		public float modifier;
		public float prelimX;
		
		public Tree(String id, Tree parent) {
			this.id = id;
			if (parent == null ) {this.parent = this;}
			else {this.parent = parent;}
		}
		
		public void add(Tree child) {children.add(child);}
		public Tree firstChild() {return children.get(0);}
		public Tree lastChild() {return children.get(children.size()-1);}
		public boolean isLeaf() {return children.size()==0;}
		public boolean hasLeftSibbling() {return index() >0;}
		
		/**The next node to the left in the same level**/
		public Tree leftNeighbor() {
			return findLeftNeighbor(level(), root(), 0, this, false);
		}
		
		private static Tree findLeftNeighbor(int targetLevel, Tree root, int level, Tree terminal, boolean found) {
			if (level == targetLevel) {return root;}
			
			for (int i=root.children.size();i >=0; i--) {
				Tree candidate = findLeftNeighbor(targetLevel, root.children.get(i), level+1, terminal, found);
				if (candidate != null && found) {return candidate;}
				else if (candidate == terminal) {found = true;}
			}
			return null;
 		}
		
		/**What level is the node found at?*/
		private int level() {
			Tree n= this;
			int level=0;
			while (n != n.parent) {
				level++;
				n = n.parent;
			}
			return level;
		}
		
		private Tree root() {
			Tree n= this;
			while (n!=n.parent) {n = n.parent;}
			return n;
		}
		
		/**What child index is the current node.
		 * The root has child index -1.
		 * @return
		 */
		private int index() {
			if (this == parent) {return -1;}
			for (int i=0; i< parent.children.size(); i++) {
				if (parent.children.get(i) == this) {return i;}
			}
			throw new RuntimeException("Malformed tree.  Node is not found in parents child list.");
		}
		
		public static Tree findNode(Tree root, String id) {
			if (root.id.equals(id)) {return root;}
			else {
				for (int i=0; i< root.children.size(); i++) {
					Tree n = findNode(root.children.get(i), id);
					if (n != null) {return n;}
				}
			}
			return null;
		}

	}

	private Tree root;

	public Tuple query(String id) {
		Tree node = Tree.findNode(root, id);
		Object[] values = new Object[]{node.x, node.y};
		return new PrototypedTuple(NAMES, values);
	}
	
	public Tuple map(String id, String parentID) {
		Tree child;
		if (root == null) {
			root = new Tree(id, null);
			child = root;
		} else {
			Tree parent = Tree.findNode(root, parentID);
			child = new Tree(id, parent);
			parent.add(child);
		}
		layout(root);
		Object[] values = new Object[]{child.x, child.y};
		return new PrototypedTuple(NAMES, values);
		
	}

	private static void layout(Tree root) {
		if (root == null) {return;}
		firstWalk(root);
		float xTopAdj = root.x - root.prelimX;
		float yTopAdj = root.y;
		secondWalk(root, 0, xTopAdj, yTopAdj);
	}
	
	private static void secondWalk(Tree node, float modSum, float xAdj, float yAdj) {
		float xTemp = xAdj + node.prelimX + modSum;
		float yTemp = yAdj + node.level() * LEVEL_SEP;
		
		node.x = xTemp;
		node.y = yTemp;
		
		for (Tree child: node.children) {
			secondWalk(child, modSum + node.modifier, xAdj, yAdj);
		}
	}
	
	private static void firstWalk(Tree node) {
		node.modifier=0;
		if (node.isLeaf()) {
			if (node.hasLeftSibbling()) {
				Tree sibbling = node.leftNeighbor();
				float meanSize = meanSize(sibbling, node);
				node.prelimX = sibbling.prelimX + SIBLING_SEP + meanSize;
			} else {
				node.prelimX =0;
			}
		} else {
			for (Tree child: node.children) {firstWalk(child);}
			float midpoint = node.firstChild().prelimX + node.lastChild().prelimX;
			if (node.hasLeftSibbling()) {
				Tree sibbling = node.leftNeighbor();
				float meanSize = meanSize(sibbling, node);
				node.prelimX = sibbling.prelimX + SIBLING_SEP + meanSize;
				node.modifier = node.prelimX - midpoint;
				apportion(node);
			} else {
				node.prelimX = midpoint;
			}
		}
	}
	
	private static void apportion(Tree node) {
		
	}
	
	private static float meanSize(Tree left, Tree right) {
		float size =0;
		
		if (left != null) {size = size +rightSize(left);}
		if (right != null) {size = size +leftSize(right);}
		
		return size;
	}
	
	private static float rightSize(Tree root) {
	}
	
	private static float leftSize(Tree root) {
		
	}
	
}
