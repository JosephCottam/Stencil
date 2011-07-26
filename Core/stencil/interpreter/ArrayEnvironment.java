package stencil.interpreter;

import java.util.Arrays;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

final class ArrayEnvironment extends Environment {
	private static final String FRAME_PREFIX = "Frame";
	private final Tuple[] frames;
	private int filledSize = 0;
	
	
	private ArrayEnvironment(int capacity) {
		frames = new Tuple[capacity];
		Arrays.fill(frames, Tuples.EMPTY_TUPLE);
	}
	
	public void setFrame(int frame, Tuple t) {
		frames[frame]=t;
	}
	
	public ArrayEnvironment extend(Tuple t) {
		if (filledSize >= frames.length) {throw new RuntimeException("Attempt to over-extend environment (max env size of " + frames.length + ")." );}
		
		frames[filledSize] = t;
		filledSize++;
		return this;
	}

	public Tuple get(int idx) {
		try {return frames[idx];}
		catch (Exception e) {throw new RuntimeException("Error de-referencing environment of size " + frames.length, e);}
	}

	public TuplePrototype prototype() {return new TuplePrototype(TuplePrototypes.defaultNames(frames.length,FRAME_PREFIX));}
	public Object get(String name) throws InvalidNameException {
		String part = name.substring(FRAME_PREFIX.length());
		int i;
		if (part.equals("")) {i=0;}
		else {i = Integer.parseInt(part);}
		
		return frames[i];
	}
	
	public boolean hasField(String name) {throw new UnsupportedOperationException();}
	public String toString() {return Tuples.toString(this);}
	public int size() {return filledSize;}
	/* (non-Javadoc)
	 * @see stencil.parser.tree.util.Environment#capacity()
	 */
	public int capacity() {return frames.length;}

	public ArrayEnvironment ensureCapacity(int capacity) {
		if (capacity <= frames.length) {return this;}
		
		ArrayEnvironment env = new ArrayEnvironment(capacity);
		System.arraycopy(frames, 0, env.frames, 0, filledSize);
		env.filledSize = this.filledSize;
		return env;
	}
	
	public boolean isDefault(String name, Object value) {return false;}

	public ArrayEnvironment clone() {
		ArrayEnvironment result = new ArrayEnvironment(frames.length);
		System.arraycopy(frames, 0, result.frames, 0, filledSize);
		result.filledSize = filledSize;
		return result;
	}
	
	/**Order of the frames:
	 * 	Canvas
	 *  View
	 *  Stream
	 *  Prefilter
	 *  Local
	 *  
	 *  If any frame is missing or not applicable, its place should be held by an empty tuple in the array.
	 *  If not enough frames are supplied, empty tuples will be added.
	 *  If more than the standard frames are supplied, the extras will still be put in the environment.
	 *  Regardless of the frames passed, the result will have at least unfilled empty frames at the end.
	 */
	final static ArrayEnvironment makeDefault(Tuple...frames) {
		int size = Math.max(DEFAULT_SIZE, frames.length);
		ArrayEnvironment e = new ArrayEnvironment(size);
		System.arraycopy(frames, 0, e.frames, 0, frames.length);
		e.filledSize = DEFAULT_SIZE;
		return e;
	}
}
