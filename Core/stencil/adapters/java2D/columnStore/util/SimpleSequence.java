/**
 * 
 */
package stencil.adapters.java2D.columnStore.util;

import java.util.Iterator;

public final class SimpleSequence implements Iterable<Integer>, Iterator<Integer> {
	final int max; 
	int current=0;

	public SimpleSequence(int max) {this.max=max;}
	
	@Override
	public boolean hasNext() {return current < max;}

	@Override
	public Integer next() {return current++;}

	@Override
	public void remove() {throw new UnsupportedOperationException();}

	@Override
	public Iterator<Integer> iterator() {return new SimpleSequence(max);}
}