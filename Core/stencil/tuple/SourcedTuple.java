package stencil.tuple;

/**Tagging interface for a tuple with a standard source and values configuration.
 * 
 * The standard configuration is to have source indicating field at the index
 * specified by SOURCE and a tuple containing the values at the index indicated
 * by VALUES; 
 */
public interface SourcedTuple extends Tuple {
	public static final int SOURCE=0;
	public static final int VALUES=1;
	
	public String getSource();
	public Tuple getValues();
	
	public static class Wrapper implements SourcedTuple {
		private final String source;
		private final Tuple base;
		
		public Wrapper(String source, Tuple base) {
			this.source = source;
			this.base = base;
		}
		
		@Override
		public Object get(int idx) throws TupleBoundsException {
			if (idx == SOURCE) {return source;}
			if (idx == VALUES) {return base;}
			throw new TupleBoundsException(idx, size());
		}

		@Override
		public int size() {return 2;}
		
		@Override
		public String getSource() {return source;}
		@Override
		public Tuple getValues() {return base;}
		@Override
		public String toString() {return Tuples.toString(this);}
		
		@Override
		public boolean equals(Object other) {return Tuples.equals(this, other);}
		
		@Override
		public int hashCode() {return Tuples.hashCode(this);}
	}
}
