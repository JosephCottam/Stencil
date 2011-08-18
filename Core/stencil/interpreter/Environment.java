package stencil.interpreter;

import java.util.Arrays;

import stencil.parser.ParserConstants;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.util.collections.ArrayUtil;

public class Environment implements Tuple, Cloneable {
	/**The default frame names.  
	 * The element "**stream**" is logically replaced by the name 
	 * of the stream in any frame instance.*/
	public static final String[] DEFAULT_FRAME_NAMES = {ParserConstants.GLOBALS_FRAME, ParserConstants.STREAM_FRAME, ParserConstants.PREFILTER_FRAME, ParserConstants.LOCAL_FRAME};
	public static final int DEFAULT_SIZE = DEFAULT_FRAME_NAMES.length;
	public static final int GLOBAL_FRAME = ArrayUtil.indexOf(ParserConstants.GLOBALS_FRAME, DEFAULT_FRAME_NAMES);
	public static final int STREAM_FRAME = ArrayUtil.indexOf(ParserConstants.STREAM_FRAME, DEFAULT_FRAME_NAMES);
	public static final int PREFILTER_FRAME = ArrayUtil.indexOf(ParserConstants.PREFILTER_FRAME, DEFAULT_FRAME_NAMES);
	public static final int LOCAL_FRAME = ArrayUtil.indexOf(ParserConstants.LOCAL_FRAME, DEFAULT_FRAME_NAMES);
	private final Tuple[] frames;
	private int filledSize = 0;
	
	
	private Environment(int capacity) {
		frames = new Tuple[capacity];
		Arrays.fill(frames, Tuples.EMPTY_TUPLE);		//TODO: Remove, will require better handling of null in tuple-refs and call chain return values though
	}
	
	public void setFrame(int frame, Tuple t) {
		frames[frame]=t;
	}
	
	public Environment extend(Tuple t) {
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

	public int capacity() {return frames.length;}

	public Environment ensureCapacity(int capacity) {
		if (capacity <= frames.length) {return this;}
		
		Environment env = new Environment(capacity);
		System.arraycopy(frames, 0, env.frames, 0, filledSize);
		env.filledSize = this.filledSize;
		return env;
	}

	public Environment clone() {
		Environment result = new Environment(frames.length);
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
	public static Environment getDefault(Tuple... tuples) {
		int size = Math.max(DEFAULT_SIZE, tuples.length);
		Environment e = new Environment(size);
		System.arraycopy(tuples, 0, e.frames, 0, tuples.length);
		e.filledSize = DEFAULT_SIZE;
		return e;
	}
}