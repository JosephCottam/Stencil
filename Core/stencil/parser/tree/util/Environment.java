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

import stencil.streams.Tuple;

public class Environment {
	private static final String NO_NAME = "$$$$$$$INVALID ID$$$$$$$$";
	private static final Environment EMPTY = new Environment() {
		public Object resolve(int field) {throw new RuntimeException("Field not index not know: " + field);}
		public Object resolve(String field) {throw new RuntimeException("Field not index not know: " + field);}
		public Object resolve(String frame, String field) {throw new RuntimeException("Frame/field reference not known:" + frame + "/" +field);}
		public Object resolve(int frame, int field) {throw new RuntimeException("Frame/field reference not known:" + frame + "/" +field);}
	};

	
	final Environment parent;
	final String name;
	final Tuple update;
	
	protected Environment() {
		parent = null;
		name = NO_NAME;
		update = null;
	}
	
	public Environment(Tuple update) {this(NO_NAME, update);}
	public Environment(String name, Tuple update) {this(EMPTY, name, update);}
	
	private Environment(Environment prior, Tuple update) {this(prior, NO_NAME, update);}
	private Environment(Environment prior, String name, Tuple update) {
		this.parent = prior;
		this.name = name;
		this.update = update;
	}

	public Environment append(Tuple t) {return new Environment(this, t);}
	public Environment append(String frame, Tuple t) {return new Environment(this, frame, t);}
	public Environment pop() {return this.parent;}

	public Object resolve(String field) {
		if (update.hasField(field)) {return update.get(field);}
		else {return parent.resolve(field);}
	}
	
	public Object resolve(String frame, String field) {
		if (name.equals(frame)) {return resolve(field);}
		else {return parent.resolve(frame, field);}
	}
	
	public Object resolve(int index) {
		return update.get(update.getFields().get(index));
	}
	
	public Object resolve(int frame, int index) {
		if (frame == 0) {return resolve(index);}
		else {return parent.resolve(frame--, index);}
	}
}
