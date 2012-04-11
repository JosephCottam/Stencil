package stencil.tuple;

import java.util.Iterator;

/**Wrap a tuple as an iterable.**/
public final class IterableTuple implements Iterable {
	private final Tuple source;
	public IterableTuple(Tuple source) {this.source = source;}
	
	@Override
	public Iterator iterator() {return new TupleIterator(source);}
	
	private static final class TupleIterator implements Iterator {
		int at=0;
		private final Tuple source;
		
		public TupleIterator(Tuple source) {this.source = source;}
		
		@Override
		public boolean hasNext() {return at<source.size();}

		@Override
		public Object next() {return source.get(at++);}

		@Override
		public void remove() {throw new UnsupportedOperationException();}
	}
}
