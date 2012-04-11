package stencil.parser.tree;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.CommonTree;

import stencil.parser.string.StencilParser;


public class StencilTree extends CommonTree implements Iterable<StencilTree> {
	public StencilTree(Token token) {super(token);}

	/**Gets the string name of the type given.
	 * Note: This uses a relatively slow method for lookup.
	 * @param type Type to be investigated
	 * @return The name associated with the type integer
	 */
	public static String typeName(int type) {
		return StencilParser.tokenNames[type];
	}
	
	@Override
	public StencilTree getAncestor(int type) {return (StencilTree) super.getAncestor(type);}
	public StencilTree getAncestor(int... types) {
		for (int type:types) {
			StencilTree anc = getAncestor(type);
			if (anc != null) {return anc;}
		}
		return null;
	}
	
	public StencilTree getChild(int i) {return (StencilTree) super.getChild(i);}
	
	/**Find an element with the given name and text equal to value.*/
	public StencilTree find(int type, String value) {
		for (StencilTree child: findAll(type)) {
			if (child.getText().equals(value)) {return child;}
		}
		throw new NoSuchElementException(String.format("No child with text of %1$s and type %2$s found.", value, typeName(type)));
	}
	
	/**Find child of this given type.  
	 * This is a proxy for getfirstchildWithType, provided for symmetry with other findChild variants.
	 **/
	public StencilTree find(int type) {return (StencilTree) this.getFirstChildWithType(type);}
	public <T> T find(Class<T> type) {
		int childCount = getChildCount();
		for (int i=0; i< childCount ; i++) {
			Object child =getChild(i);
			if (type.isInstance(child)) {return (T) child;}
		}
		return null;
	}
	
	
	/**Find a direct descendants from the given type list;
	 * if more than one child from the type list is found, an assertion error occurs (if assertions are enabled).*/
	public StencilTree find(int... types) {
		java.util.List<StencilTree> l = searchDescendants(false, types);
		
		assert l.size() ==1 : "Searched for single child, but " + l.size() + " children found";
		if (l.size() ==1) {return l.get(0);}
		else {return null;}
	}
	
	
	/**Find all direct descendants of the given type.*/
	public java.util.List<StencilTree> findAll(int... types) {
		return searchDescendants(false, types);
	}
	
	/**Find an eventual descendants from the given type list;
	 * if more than one child from the type list is found, an assertion error occurs (if assertions are enabled).*/
	public StencilTree findDescendant(int... types) {
		java.util.List<? extends CommonTree> l = searchDescendants(true, types);
		
		
		if (l.size() == 1) {return (StencilTree) l.get(0);}
		if (l.size() == 0) {return null;}
		throw new RuntimeException("Searched for single child, but multiple children found " + l.size());
	}
	

	
	/**Recursively look for descendants of the given type.*/
	public java.util.List<StencilTree> findAllDescendants(int... types) {
		return searchDescendants(true, types);
	}

	private java.util.List<StencilTree> searchDescendants(boolean recursive, int... types) {
		java.util.List<StencilTree> results = new ArrayList();
		if (this.getChildren() == null) {return results;}
		List<Integer> typeIds = new ArrayList(); 
		for (int i: types)  {typeIds.add(i);}
		Collections.sort(typeIds);
		
		for (Object child: getChildren()) {
			if (child instanceof StencilTree && typeIds.contains(((Tree) child).getType())) {
				results.add((StencilTree) child);
			}
			if (recursive && child instanceof StencilTree) {
				results.addAll(((StencilTree) child).searchDescendants(recursive, types));
			}
		}
		return results;
	}
		
	@Override
	public StencilTree dupNode() {
		try {
			Constructor c = this.getClass().getConstructor(Token.class);
			return (StencilTree) c.newInstance(this.getToken());
		} catch (Exception e) {
			throw new Error(String.format("Error reflectively duplicating node to node of same type (%1$s).", this.getClass().getName()), e);
		}
	}

	@Override
	public StencilTree getParent() {return (StencilTree) super.getParent();}

	public static final class ChildrenIterator<T> implements Iterator<T> {
		int idx=0 ;
		final int max;
		final Tree root;
		
		public ChildrenIterator(Tree root) {
			max = root.getChildCount();
			this.root = root;
		}
		
		public boolean hasNext() {return idx < max;}
		public T next() {return (T) root.getChild(idx++);}
		public void remove() {throw new UnsupportedOperationException("Cannot remove from a Stencil Tree List.");}			
	}
	
	public int hashCode() {
		int code =getText().hashCode() * getType();

		for (StencilTree child: this) {
			int childCode = child.hashCode();
			code = code * (childCode ==0 ? 2 : childCode);
		}
		return code;
	}
	
	public boolean equals(Object othr) {
		if (othr == null || !(othr instanceof StencilTree)) {return false;}
		StencilTree other = (StencilTree) othr;
		
		if (other.getType() != this.getType()) {return false;}
		
		if (other.getText() == null) {return getText() == null;}
		if (!other.getText().equals(getText())
				|| other.getChildCount() != getChildCount()) {return false;}
		
		for (int i=0; i< getChildCount(); i++) {
			if (!getChild(i).equals(other.getChild(i))) {return false;}
		}
				
		return true;
	}
	
	public Iterator<StencilTree> iterator() {return new ChildrenIterator(this);}
	
	public boolean is(int type) {return this.getType() == type;}
}
