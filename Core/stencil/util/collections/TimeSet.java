package stencil.util.collections;

import java.util.Iterator;
import java.util.Calendar;


public class TimeSet<T> extends ConditionSet<T, Long> {
	private int maxAge=1000;

	/**Maximum age in milliseconds.*/
	public TimeSet(int maxAge) {this.maxAge = maxAge;}

	/**Indicate that a sweep should be made and a new pass started.*/
	public void sweep() {
		long oldest = Calendar.getInstance().getTimeInMillis();
		oldest = oldest - maxAge;

		Iterator<Entry> i = contents.iterator();
		while (i.hasNext()) {
			Entry e = i.next();
			if (e.mark < oldest) {i.remove();}
		}
	}

	public Entry wrap(T o) {
		Long l  =Calendar.getInstance().getTimeInMillis();
		return new Entry(l, o);
	}
}
