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
	public static final int CANVAS = 0;
	public static final int VIEW = 1;
	public static final int STREAM = 2;
	public static final int LOCAL = 3;
	
	/**Proxy object that indicates that the return value should be ignored
	 * an no new environment frame should be created.
	 * 
	 * THIS SHOULD ONLY BE RETURNED BY STENCIL INTERNAL METHODS.  Stencil semantics
	 * cannot be guaranteed if this is returned by other methods.  It is public
	 * so the internal operators can have access to it.  Utility methods of
	 * stencil use this return value so utility operators can be inserted
	 * in call chains without interfering with literal positional values
	 * de-referenced on the environment.
	 * 
	 * TODO: REMOVE!!!  Do not allow positional de-referencing to environment frames and translate names after conversions, autoguide, etc. that may add frames run
	 */
	public static Tuple NO_NEW_FRAME = new Tuple() {
		public Object get(String name) {throw new Error("Should never be invoked.");}
		public Object get(int idx) {throw new Error("Should never be invoked.");}
		public TuplePrototype getPrototype() {throw new Error("Should never be invoked.");}
		public boolean isDefault(String name, Object value) {throw new Error("Should never be invoked.");}
		public int size() {throw new Error("Should never be invoked.");}		
	};
	
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

	public Tuple get(int idx) {return frames[idx];}

	public TuplePrototype getPrototype() {return prototype;}
	public Object get(String name) throws InvalidNameException {
		return Tuples.namedDereference(name, this);
	}
	public boolean hasField(String name) {return getPrototype().contains(name);}

	public int size() {return frames.length;}
	
	public boolean isDefault(String name, Object value) {return false;}

	/**Create the default environment from the passed tuples.*/
	public final static Environment getDefault(Tuple canvas, Tuple view, Tuple stream) {
		return getDefault(canvas, view, stream, null);
	}
	
	public final static Environment getDefault(Tuple canvas, Tuple view, Tuple stream, Tuple local) {
		int size = (local == null ? 3 :4);
		Environment e = new Environment(size);
		e.frames[CANVAS] = canvas;
		e.frames[VIEW] = view;
		e.frames[STREAM] = stream;
		if (local != null) {e.frames[LOCAL] = local;}
		return e;		
	}
}
