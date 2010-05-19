package stencil.util.collections;

import java.util.ArrayList;
import java.util.List;

public class Tree<D extends Tree> {
	protected D parent;
	final protected Object id;
	final protected List<D> children;
	
	public Tree(Object id, D parent) {
		this.id = id;
		this.parent = parent != null ? parent : (D) this;
		children = new ArrayList(2);
	}
	
	public List<D> children() {return children;}
	public void addChild(D child) {
		children.add(child);
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
}
