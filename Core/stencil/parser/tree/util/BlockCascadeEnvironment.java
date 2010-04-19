/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.parser.tree.util;

import stencil.parser.ParserConstants;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.collections.ArrayUtil;

final class BlockCascadeEnvironment extends Environment {
	/**The default frame names.  
	 * The element "**stream**" is logically replaced by the name 
	 * of the stream in any frame instance.*/
	public static final String[] DEFAULT_FRAME_NAMES = {ParserConstants.CANVAS_FRAME, ParserConstants.VIEW_FRAME, "stream", ParserConstants.PREFILTER_FRAME, ParserConstants.LOCAL_FRAME};	
	public static final int DEFAULT_SIZE = DEFAULT_FRAME_NAMES.length;
	public static final int CANVAS_FRAME = ArrayUtil.indexOf(ParserConstants.CANVAS_FRAME, DEFAULT_FRAME_NAMES);
	public static final int VIEW_FRAME = ArrayUtil.indexOf(ParserConstants.VIEW_FRAME, DEFAULT_FRAME_NAMES);
	public static final int STREAM_FRAME = 2;
	public static final int PREFILTER_FRAME = ArrayUtil.indexOf(ParserConstants.PREFILTER_FRAME, DEFAULT_FRAME_NAMES);
	public static final int LOCAL_FRAME = ArrayUtil.indexOf(ParserConstants.LOCAL_FRAME, DEFAULT_FRAME_NAMES);
	
	private static final Environment EMPTY = new Environment() {
		public int size() {return 0;}
		public int capacity() {return 0;}
		public ArrayEnvironment extend(Tuple t) {throw new UnsupportedOperationException();}
		public ArrayEnvironment extendCapacity(int capacity) {throw new UnsupportedOperationException();}
		public void setFrame(int frame, Tuple t) {throw new UnsupportedOperationException();}
		public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
		public Tuple get(int idx) throws TupleBoundsException {throw new UnsupportedOperationException();}
		public TuplePrototype getPrototype() {throw new UnsupportedOperationException();}
		public boolean isDefault(String name, Object value) {return false;}
	};
	
	private final Tuple[] frames;
	private final Environment parent;
	private int size = 0;
	
	private BlockCascadeEnvironment(int capacity) {this(capacity, EMPTY);}
	private BlockCascadeEnvironment(int capacity, Environment parent) {
		frames = new Tuple[capacity];
		this.parent = parent;
	}
	
	
	/**Explicitly set a frame to a particular value.*/
	public void setFrame(int frame, Tuple t) {
		int inCascade = frame-parent.capacity();
		if (inCascade < 0) {
			parent.setFrame(frame, t);
		} else {
			frames[inCascade]=t;
			size = Math.max(size, inCascade);
		}
	}
	
	public BlockCascadeEnvironment extend(Tuple t) {
		if (size >= frames.length) {throw new RuntimeException("Attempt to over-extend environment (max env size of " + frames.length + ")." );}
		
		frames[size] = t;
		size ++;
		return this;
	}

	public Tuple get(int idx) {
		try {
			if (idx < parent.capacity()) {return (Tuple) parent.get(idx);}
			else {return frames[idx-parent.capacity()];}
		} catch (Exception e) {throw new RuntimeException("Error de-referencing environment of size " + frames.length, e);}
	}

	public TuplePrototype getPrototype() {throw new UnsupportedOperationException();}
	public Object get(String name) throws InvalidNameException {throw new UnsupportedOperationException();}
	public boolean hasField(String name) {throw new UnsupportedOperationException();}

	public int size() {return size + parent.capacity();}
	public int capacity() {return frames.length + parent.capacity();}
	
	public Environment extendCapacity(int additionalCapacity) {
		if (size != frames.length) {throw new RuntimeException("Cannot extend capacity of an undeful block cascade environment.");}
		return new BlockCascadeEnvironment(additionalCapacity, this);
	}
	
	public boolean isDefault(String name, Object value) {return false;}

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
	 *  
	 *  After creation, the new environment may or may not be independent of
	 *  the tuple array originally passed.
	 */
	public final static BlockCascadeEnvironment getDefault(Tuple...frames) {
		int size = Math.max(DEFAULT_SIZE, frames.length);
		BlockCascadeEnvironment e = new BlockCascadeEnvironment(size);
		System.arraycopy(frames, 0, e.frames, 0, frames.length);
		for (int i=frames.length; i<size; i++) {e.frames[i] = Tuples.EMPTY_TUPLE;}
		e.size = e.frames.length;
		return e;
	}
}
