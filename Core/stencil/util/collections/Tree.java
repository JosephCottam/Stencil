package stencil.util.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tree<D extends Tree> {
	protected D parent;
	final protected Object id;
	final protected List<D> children;
	
	public Tree(Object id) {this(id, null);}
	public Tree(Object id, D parent) {
		this.id = id;
		children = new ArrayList(2);
		if (parent != this && parent != null) {parent.addChild(this);}
		if (this.parent == null) {this.parent = (D) this;}
	}
	
	public List<D> children() {return children;}
	public void addChild(D child) {
		children.add(child);
		if (child.getParent() != null) {
			child.getParent().removeChild(child);
		}
		child.setParent(this);
	}

	/**What is the index of the given child?*/
	public int childIdx(Tree child) {return children.indexOf(child);}
	
	/**Which child is this in its parent's list?*/
	public int getIdx() {return parent.childIdx(this);}
	
	
	public void removeChild(Tree child) {removeChild(children.indexOf(child));}
	public void removeChild(int i) {
		if (i<0 || i >= children.size()) {return;}
		children.remove(i);
	}
	
	public Object getID() {return id;}
	
	public void setParent(D parent) {this.parent = parent;}
	public D getParent() {return parent;}
	public boolean isRoot() {return parent==this;}
	
	@Override
	public String toString() {return toString(0);}
	
	private String toString(int nest) {
		String pad = new String();
		for (int i=0; i<nest; i++) {pad = pad + "  ";}
		
		StringBuilder children = new StringBuilder();
		for (Tree child: children()) {
			children.append(child.toString(nest+1));
		}
		
		return pad + id + "\n" + children;
	}
	
	/**What is the path to this node?
	 * Path is the child indicies.  The root has a path of zero-length.
	 * 
	 * @return
	 */
	public Integer[] getPath() {
		if (parent == this) {return new Integer[0];}
		
		LinkedList<Integer> path = new LinkedList();
		Tree node = this;
		while (node != node.parent) {
			path.push(node.getIdx());
			node = node.parent;
		}
		
		return path.toArray(new Integer[path.size()]);		
	}
	
	
    
    /**Search the tree for the given ID.  
     * Return the node if found, otherwise return null.
     */
    public static <C extends Tree> C findNode(Tree<C> root, Object id) {
    	if (root == null) {return null;}
    	
    	if (root.getID() != null && root.getID().equals(id)) {return (C) root;}
    	for (Tree<C> child: root.children()) {
    		Tree<C> c = findNode(child, id);
    		if (c!= null) {return (C) c;}
    	}
    	return null;
    }

}
