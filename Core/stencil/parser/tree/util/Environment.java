package stencil.parser.tree.util;

import stencil.parser.ParserConstants;
import stencil.tuple.Tuple;
import stencil.util.collections.ArrayUtil;

public abstract class Environment implements Tuple {
	/**The default frame names.  
	 * The element "**stream**" is logically replaced by the name 
	 * of the stream in any frame instance.*/
	public static final String[] DEFAULT_FRAME_NAMES = {ParserConstants.CANVAS_FRAME, ParserConstants.VIEW_FRAME, "**stream**", ParserConstants.PREFILTER_FRAME, ParserConstants.LOCAL_FRAME};
	public static final int DEFAULT_SIZE = DEFAULT_FRAME_NAMES.length;
	public static final int CANVAS_FRAME = ArrayUtil.indexOf(ParserConstants.CANVAS_FRAME, DEFAULT_FRAME_NAMES);
	public static final int VIEW_FRAME = ArrayUtil.indexOf(ParserConstants.VIEW_FRAME, DEFAULT_FRAME_NAMES);
	public static final int STREAM_FRAME = 2;
	public static final int PREFILTER_FRAME = ArrayUtil.indexOf(ParserConstants.PREFILTER_FRAME, DEFAULT_FRAME_NAMES);
	public static final int LOCAL_FRAME = ArrayUtil.indexOf(ParserConstants.LOCAL_FRAME, DEFAULT_FRAME_NAMES);

	/**Explicitly set a frame to a particular value.*/
	public abstract void setFrame(int frame, Tuple t);

	public abstract Environment extend(Tuple t);

	public abstract int capacity();
	
	public abstract Tuple get(int idx);

	/**Returns an environment with the same contents but potentially different
	 * capacity than the original.  If the requested capacity is the
	 * same or less than the current capacity, the environment will be returned;
	 * 
	 * Otherwise, a new environment with at least the requested capacity is returned.
	 * 
	 * The environment size should not change, but the capacity does.
	 */
	public abstract Environment extendCapacity(int capacity);
	
	public static Environment getDefault(Tuple... tuples) {
		return ArrayEnvironment.getDefault(tuples);
	}

}