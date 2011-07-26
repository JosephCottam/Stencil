package stencil.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**A list that does not allow null or duplicates.
 * If an attempt to place a null or duplicate item into the list is made, an IllegalArgumentException is thrown.
 * As a convenience, a 'move' method has been provided to support changing an element's position.
 *
 */
public class ListSet<T> extends ArrayList<T> implements Set<T> {
	private static final long serialVersionUID = 1L;

	public ListSet(T... values) {super(java.util.Arrays.asList(values));}

	public ListSet(Collection<T> source) {
		for (T value:source) {this.add(value);}
	}

	public void  add(int i, T element) {
		validateItem(element);
		super.add(i, element);
	}
	public boolean add(T element) {
		validateItem(element);
		return super.add(element);
	}

	public boolean addAll(Collection<? extends T> c) {
		for (T g:c) {validateItem(g);}
		return super.addAll(c);
	}
	
	/**Add elements to the set, only if they are not already present.**/
	public boolean addUnique(Collection<? extends T> c) {
		boolean modified = false;
		for (T g: c) {
			if (this.contains(g)) {continue;}
			add(g);
			modified = true;
		}
		return modified;
	}
	
	public T set(int index, T element) {
		validateItem(element);
		return super.set(index, element);
	}

	/**Move the element to the target index.
	 * This method only works if the element is the list, otherwise an IllegalArgumentException is thrown.
	 */
	public void move(int index, T element){
		if (!super.contains(element)) {throw new IllegalArgumentException("Cannot move item not in the list.");}
		super.remove(element);
		super.add(index, element);
	}

	private boolean validateItem(Object candidate) {
		if (candidate == null) {throw new IllegalArgumentException("Null values not permitted.");}
		else if (this.contains(candidate)) {throw new IllegalArgumentException("Duplicate values not permitted.  Use move instead.");}
		return true;
	}
}