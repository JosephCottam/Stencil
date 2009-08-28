package stencil.util.collections;

import java.util.Iterator;

/**Set for tracking transient items with unknown lifetimes.
 *
 * This set works by dividing time into a number of passes over the data.
 * If an item is not 'touched' between calls to 'sweep()' it will be removed
 * during the call to sweep.  Touching an item is either adding or checking for it.
 *
 * Removal from the set happens during the sweep call.
 *
 */
public final class MarkSweepSet<T> extends ConditionSet<T, Integer> {

	protected class Entry extends ConditionSet.Entry {

		public Entry(Integer m, T value) {super(m, value);}

		public boolean equals(Object o) {
			if (!(o.getClass().equals(Entry.class))) {return false;}

			Entry e = (Entry) o;
			if (this == e || value.equals(e.value)) {
				mark = newMark;
				e.mark = newMark; //mark both if compared.
				return true;
			}
			return false;
		}
	}

	protected int newMark=Integer.MIN_VALUE;

	/**Indicate that a sweep should be made and a new pass started.*/
	public void sweep() {
		Iterator<ConditionSet<T,Integer>.Entry> i = contents.iterator();
		while (i.hasNext()) {
			int mark = i.next().mark;
			if (mark != newMark) {i.remove();}
		}

		if (newMark == Integer.MAX_VALUE) {newMark = Integer.MIN_VALUE;}
		else {newMark++;}
	}

	public Entry wrap(T o) {return new Entry(newMark, o);}
}
