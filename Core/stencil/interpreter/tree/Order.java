package stencil.interpreter.tree;

import java.util.Iterator;

public class Order implements Iterable<String[]>{
	private final String[][] clauses;
	public Order(String[][] clauses) {this.clauses = clauses;}

	public Iterator<String[]> iterator() {
		return new IteratorTarget();
	}
	
	/**What items are in the n-th group?*/
	public String[] getGroup(int idx) {return clauses[idx];}

	/**Array iterator for teh groups*/
	private final class IteratorTarget implements Iterator<String[]> {
		private int current=0;
		private int max=clauses.length;

		public boolean hasNext() {return current < max;}
		public String[] next() {return clauses[current++];}
		public void remove() {throw new UnsupportedOperationException();}
	}
}
