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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;

public class Environment implements Tuple {
 	private static final  class UnknownNameException extends RuntimeException {
		String desired;
		List<String> valid;
		public UnknownNameException(String name, List<String> valid) {
			this.desired = name;
			this.valid = valid;
		}
		
		public UnknownNameException(UnknownNameException base, String frame, List<String> valid) {
			this.desired = base.desired;
			this.valid = new ArrayList<String>();
			this.valid.add(frame);
			this.valid.addAll(valid);
			this.valid.addAll(base.valid);
		}
		
		public String getMessage() {return String.format("Could not find %1$s in %2$s", desired, Arrays.deepToString(valid.toArray()));}
	}
	
	private static final String NO_NAME = "$$$$$$$INVALID ID$$$$$$$$";
 	private static final Environment EMPTY = new Environment() {
	 		private final List<String> EMPTY_LIST = new ArrayList<String>();
			public Object get(String field) {throw new UnknownNameException(field, EMPTY_LIST);}
			public Object get(int idx) {throw new TupleBoundsException(idx, size());}
			public Environment popTo(int idx) {throw new RuntimeException("Cannot pop past an empty environment.");}
			public Environment pop() {throw new RuntimeException("Cannot pop past an empty environment.");}
			public List<String> getPrototype() {return EMPTY_LIST;}
			public int size() {return 0;}
			protected int depth() {return 0;} 
		};

		
	final Environment parent;
	final String frameName;
	final Tuple update;
	
	protected Environment() {
		parent = EMPTY;
		frameName = NO_NAME;
		update = null;
	}
	
	public Environment(Tuple update) {this((String) null, update);}
	public Environment(String name, Tuple update) {this(EMPTY, name, update);}
	
	private Environment(Environment prior, Tuple update) {this(prior, null, update);}
	private Environment(Environment prior, String name, Tuple update) {
		this.parent = prior;
		this.frameName = name != null ? name : NO_NAME;
		this.update = update;
	}

	public Environment append(Tuple t) {return new Environment(this, t);}
	public Environment append(String frame, Tuple t) {return new Environment(this, frame, t);}
	public Environment pop() {return this.parent;}
	public Environment popTo(int depth) {
		if (depth ==0) {return this;}
		else {return popTo(depth--);}
	}
			
	public Object get(int idx) {
		if (idx < update.size()) {return update.get(idx);}
		else {return popTo(idx-update.size());}
	}

	public Object get(String name) throws InvalidNameException {
		if (update.getPrototype().contains(name)) {return update.get(name);}
		else if (this.frameName.equals(name)){return update;}
		else {
			try {return parent.get(name);}
			catch (UnknownNameException e) {throw new UnknownNameException(e, frameName, update.getPrototype());}
		}
	}

	//TODO: This is mostly unsuitable for frame-dereferencing.  It will work for default-reference and error reporting though!
	public List<String> getPrototype() {
		ArrayList fields = new ArrayList();
		fields.addAll(update.getPrototype());
		if (frameName != NO_NAME) {fields.add(frameName);}
		fields.addAll(parent.getPrototype());
		return fields;
	}

	public boolean hasField(String name) {
		try {get(name);}
		catch (Exception e) {return false;}
		return true;
	}

	public int size() {return depth() + update.size();}
	protected int depth() {return 1 + parent.depth();}
	
	public boolean isDefault(String name, Object value) {return false;}
}
