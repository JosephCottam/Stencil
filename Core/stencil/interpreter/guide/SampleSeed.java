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
package stencil.interpreter.guide;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
 

/**Report from the echo operator for the guide system.
 * This is combined with the guide specializer to create the
 * sample descriptor.
 **/
public class SampleSeed<T> implements Iterable<T> {
	private final boolean range;
	private final List<T> elements;
	private final Class type;
	
	public SampleSeed(boolean range, List<T> elements) {
		this.range = range;
		this.elements = elements;
		if (elements.size() >0) {
			this.type = elements.get(0).getClass();
		} else {
			this.type = Object.class;
		}
	}
	
	public SampleSeed(boolean range, T... elements) {
		this.range = range;
		this.elements = Arrays.asList(elements);
		this.type = elements.getClass().getComponentType();
	}
	
	public int size() {return elements.size();}
	public boolean isRange() {return range;}
	
	/**What type of elements are in this seed?*/
	public Class getType() {return type;}

	public T get(int i) {return elements.get(i);}
	public Iterator<T> iterator() {return elements.iterator();}
}
