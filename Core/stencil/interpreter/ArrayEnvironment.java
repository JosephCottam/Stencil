package stencil.interpreter;

import java.util.Arrays;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

final class ArrayEnvironment extends Environment {
	private final Tuple[] frames;
	private int filledSize = 0;
	
	
	private ArrayEnvironment(int capacity) {
		frames = new Tuple[capacity];
		Arrays.fill(frames, Tuples.EMPTY_TUPLE);
	}
	
	@Override	
	public void setFrame(int frame, Tuple t) {
		frames[frame]=t;
	}
	
	@Override
	public ArrayEnvironment extend(Tuple t) {
		if (filledSize >= frames.length) {throw new RuntimeException("Attempt to over-extend environment (max env size of " + frames.length + ")." );}
		
		frames[filledSize] = t;
		filledSize++;
		return this;
	}

	@Override
	public Tuple get(int idx) {
		try {return frames[idx];}
		catch (Exception e) {throw new RuntimeException("Error de-referencing environment of size " + frames.length, e);}
	}

	@Override
	public String toString() {return Tuples.toString(this);}

	@Override
	public int size() {return filledSize;}

	@Override
	public int capacity() {return frames.length;}

	@Override
	public ArrayEnvironment ensureCapacity(int capacity) {
		if (capacity <= frames.length) {return this;}
		
		ArrayEnvironment env = new ArrayEnvironment(capacity);
		System.arraycopy(frames, 0, env.frames, 0, filledSize);
		env.filledSize = this.filledSize;
		return env;
	}

	@Override
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
