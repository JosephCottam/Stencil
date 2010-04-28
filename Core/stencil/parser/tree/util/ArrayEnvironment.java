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

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

final class ArrayEnvironment extends Environment {
	private static final String FRAME_PREFIX = "Frame";
	private final Tuple[] frames;
	private int filledSize = 0;
	
	
	private ArrayEnvironment(int capacity) {
		frames = new Tuple[capacity];
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

	public TuplePrototype getPrototype() {return new SimplePrototype(TuplePrototypes.defaultNames(frames.length,FRAME_PREFIX));}
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

	public ArrayEnvironment extendCapacity(int capacity) {
		if (capacity <= frames.length) {return this;}
		
		ArrayEnvironment env = new ArrayEnvironment(capacity);
		System.arraycopy(frames, 0, env.frames, 0, filledSize);
		env.filledSize = this.filledSize;
		return env;
	}
	
	public boolean isDefault(String name, Object value) {return false;}

	public ArrayEnvironment clone() {
		ArrayEnvironment result = new ArrayEnvironment(frames.length);
		System.arraycopy(frames, 0, result.frames, 0, frames.length);
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
	 *  
	 */
	public final static ArrayEnvironment getDefault(Tuple...frames) {
		int size = Math.max(DEFAULT_SIZE, frames.length);
		ArrayEnvironment e = new ArrayEnvironment(size);
		System.arraycopy(frames, 0, e.frames, 0, frames.length);
		for (int i=frames.length; i<size; i++) {e.frames[i] = Tuples.EMPTY_TUPLE;}
		e.filledSize = DEFAULT_SIZE;
		return e;
	}
}
