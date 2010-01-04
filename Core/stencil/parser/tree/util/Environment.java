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
	
	private Environment(int size) {
		frames = new Tuple[size];
		prototype = new SimplePrototype(TuplePrototypes.defaultNames(frames.length, "Frame"));
	}
	
	private Environment(Environment prior, Tuple update) {
		Tuple[] source = prior.frames;
		frames = new Tuple[source.length+1];
		frames[source.length] = update;
		System.arraycopy(source, 0, frames, 0, source.length);
		prototype = new SimplePrototype(TuplePrototypes.defaultNames(frames.length, "Frame"));
	}

	public Environment push(Tuple t) {
		return new Environment(this, t);
	}

	public Tuple get(int idx) {
		try {return frames[idx];}
		catch (Exception e) {throw new Error("Error de-referencing environment of size " + frames.length, e);}
	}

	public TuplePrototype getPrototype() {return prototype;}
	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}
	public boolean hasField(String name) {return getPrototype().contains(name);}

	public int size() {return frames.length;}
	
	public boolean isDefault(String name, Object value) {return false;}

	/**Create the default environment from the passed tuples.*/
	public final static Environment getDefault(Tuple canvas, Tuple view, Tuple stream) {
		Environment e = new Environment(DEFAULT_FRAME_SIZE);
		e.frames[CANVAS_FRAME] = canvas;
		e.frames[VIEW_FRAME] = view;
		e.frames[STREAM_FRAME] = stream;

		return e;
	}
}
