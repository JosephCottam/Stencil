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
package stencil.tuple;

import stencil.parser.ParserConstants;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

/**Tagging interface for a tuple with a standard source and values configuration.
 * 
 * The standard configuration is to have source indicating field at the index
 * specified by SOURCE and a tuple containing the values at the index indicated
 * by VALUES; 
 */
public interface SourcedTuple extends Tuple {
	public static final int SOURCE=0;
	public static final int VALUES=1;
	public static final TuplePrototype PROTOTYPE = new SimplePrototype(new String[]{ParserConstants.SOURCE_FIELD, ParserConstants.VALUES_FIELD}, new Class[]{String.class, Tuple.class});
	
	public String getSource();
	public Tuple getValues();
	
	public static class Wrapper implements SourcedTuple {
		private final String source;
		private final Tuple base;
		
		public Wrapper(String source, Tuple base) {
			this.source = source;
			this.base = base;
		}
		
		public Object get(String name) throws InvalidNameException {
			if (ParserConstants.SOURCE_FIELD.equals(name)) {return source;}
			if (ParserConstants.VALUES_FIELD.equals(name)) {return base;}
			throw new InvalidNameException(name, PROTOTYPE);
		}
		
		public Object get(int idx) throws TupleBoundsException {
			if (idx == SOURCE) {return source;}
			if (idx == VALUES) {return base;}
			throw new TupleBoundsException(idx, size());
		}
		
		public TuplePrototype getPrototype() {return PROTOTYPE;}

		public boolean isDefault(String name, Object value) {
			if (ParserConstants.SOURCE_FIELD.equals(name)) {return false;}
			else {return base.isDefault(name, value);}
		}
		
		public int size() {return 2;}
		
		public String getSource() {return source;}
		public Tuple getValues() {return base;}
		public String toString() {return Tuples.toString(this);}
		
		public boolean equals(Object other) {return Tuples.equals(this, other);}
	}
}
