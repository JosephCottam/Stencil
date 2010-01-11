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

public class Environment implements Tuple {
	private static final int DEFAULT_FRAME_SIZE = 3;
	public static final int CANVAS_FRAME = 0;
	public static final int VIEW_FRAME = 1;
	public static final int STREAM_FRAME =2;
	
	private final Tuple[] frames;
	private final TuplePrototype prototype;
	private int size = 0;
	
	
	private Environment(int capacity) {
		frames = new Tuple[capacity];
		prototype = new SimplePrototype(TuplePrototypes.defaultNames(frames.length, "Frame"));
	}
	
	public Environment extend(Tuple t) {
		if (size >= frames.length) {throw new RuntimeException("Attempt to over-extend environment");}
		
		frames[size] = t;
		size ++;
		return this;
	}

	public Tuple get(int idx) {
		try {return frames[idx];}
		catch (Exception e) {throw new RuntimeException("Error de-referencing environment of size " + frames.length, e);}
	}

	public TuplePrototype getPrototype() {return prototype;}
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}
	public boolean hasField(String name) {return getPrototype().contains(name);}

	public int size() {return size;}
	public int capacity() {return frames.length;}

	/**Returns an environment with the same contents but potentially different
	 * capacity than the original.  If the requested capacity is the
	 * same or less than the current capacity, the environment will be returned;
	 * 
	 * Otherwise, a new environment with at least the requested capacity is returned.
	 * 
	 * The environment size does not change, but the capacity does.
	 */
	public Environment ensureCapacity(int capacity) {
		if (capacity <= size) {return this;}
		
		Environment env = new Environment(capacity);
		System.arraycopy(frames, 0, env.frames, 0, size);
		env.size = this.size;
		return env;
	}
	
	public boolean isDefault(String name, Object value) {return false;}

	/**Create the default environment from the passed tuples.*/
	public final static Environment getDefault(Tuple canvas, Tuple view, Tuple stream) {
		return getDefault(canvas, view, stream, 0);
	}
	public final static Environment getDefault(Tuple canvas, Tuple view, Tuple stream, int additionalCapacity) {
		Environment e = new Environment(DEFAULT_FRAME_SIZE + additionalCapacity);
		e.frames[CANVAS_FRAME] = canvas;
		e.frames[VIEW_FRAME] = view;
		e.frames[STREAM_FRAME] = stream;
		e.size = DEFAULT_FRAME_SIZE;
		return e;
	}
}
