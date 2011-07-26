/**
 * 
 */
package stencil.adapters.java2D.columnStore.util;

import java.util.Iterator;

import stencil.adapters.java2D.columnStore.ColumnStore;

public final class TupleIterator implements Iterator<StoreTuple>, Iterable<StoreTuple> {
	final ColumnStore<? extends StoreTuple> source;
	final Iterable<Integer> order;
	final Iterator<Integer> position;
	final StoreTuple tuple;

	public TupleIterator(ColumnStore<? extends StoreTuple> source, Iterable<Integer> order) {this(source, order, false);}
	public TupleIterator(ColumnStore<? extends StoreTuple> source) {this(source, false);}
	public TupleIterator(ColumnStore<? extends StoreTuple> source, boolean recyleTuple) {
		this(source, new SimpleSequence(source.size()), recyleTuple);
	}
	
	/**Modify the tuple rather than returning a new one.
	 * This is safe when the lifetime of the tuple is known (rendering and pre-rendering task often benefit from such recycling). 
	 * @param basis
	 * @param recyleTuple
	 */
	public TupleIterator(ColumnStore<? extends StoreTuple> source, Iterable<Integer> order, boolean recyleTuple) {
		this.source = source;
		this.order = order;
		this.position = order.iterator();
		
		if (recyleTuple && source.size() >0) {	//Only create the tuple if there are tuples to return; otherwise...don't
			this.tuple = source.get(0);				
		} else {this.tuple = null;}
	}
	
	@Override
	public boolean hasNext() {return position.hasNext();}

	@Override
	public StoreTuple next() {
		if (tuple == null) {return source.get(position.next());}
		else {tuple.setRow(position.next()); return tuple;}
	}

	@Override
	public void remove() {throw new UnsupportedOperationException();}

	@Override
	public Iterator<StoreTuple> iterator() {return new TupleIterator(source, order, tuple!=null);}
}