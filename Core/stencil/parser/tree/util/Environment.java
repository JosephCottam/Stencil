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
import java.util.List;

import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.types.Converter;

public class Environment implements Tuple {
	private static final String NO_NAME = "$$$$$$$INVALID ID$$$$$$$$";
 	private static final Environment EMPTY = new Environment() {
 		private final List<String> EMPTY_LIST = new ArrayList<String>();
		public Object get(String field) {throw new RuntimeException("Field not know: " + field);}
		public List<String> getFields() {return EMPTY_LIST;}
	};
	
	final Environment parent;
	final String frameName;
	final Tuple update;
	
	protected Environment() {
		parent = null;
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
			
	public Object get(String name) throws InvalidNameException {
		if (update.hasField(name)) {return update.get(name);}
		else if (this.frameName.equals(name)){return update;}
		else {return parent.get(name);}
	}

	public Object get(String name, Class<?> type)
			throws IllegalArgumentException, InvalidNameException {
		return Converter.convert(get(name), type);
	}

	//TODO: This is mostly unsuitable for frame-dereferencing.  It will work for default-reference and error reporting though!
	public List<String> getFields() {
		ArrayList fields = new ArrayList();
		fields.addAll(update.getFields());
		if (frameName != NO_NAME) {fields.add(frameName);}
		fields.addAll(parent.getFields());
		return fields;
	}

	public boolean hasField(String name) {
		try {get(name);}
		catch (Exception e) {return false;}
		return true;
	}

	public boolean isDefault(String name, Object value) {return false;}
}
