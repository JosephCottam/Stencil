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
import static stencil.interpreter.guide.SampleSeed.SeedType.*;

/**Report from the echo operator for the guide system.
 * This is combined with the guide specializer to create the
 * sample descriptor.
 **/
public class SampleSeed<T> implements Iterable<T> {
	public enum SeedType {CATEGORICAL, CONTINUOUS, MIXED}
	
	private final SeedType seedType;
	private final List<T> elements;
		
	public SampleSeed(SampleSeed cont, SampleSeed cat) {		
		if (cont.size() ==0) {seedType=CATEGORICAL; elements = cat.elements;}
		else if (cat.size() ==0) {seedType=CONTINUOUS; elements = cont.elements;}
		else {
			seedType = MIXED;
			elements = (List<T>) Arrays.asList(cont, cat);
		}
	}
	public SampleSeed(SeedType seedType, List<T> elements) {
		this.seedType = seedType;
		this.elements = elements;
	}

	public int size() {return elements.size();}

	public boolean isMixed() {return seedType == MIXED;}
	public boolean isCategorical() {return seedType == CATEGORICAL;}	
	public boolean isContinuous() {return seedType == CONTINUOUS;}
	
	public SampleSeed getCategorical() throws UnsupportedOperationException {
		if (seedType == CATEGORICAL){return this;}
		if (seedType == MIXED) {return (SampleSeed) elements.get(1);}
		throw new UnsupportedOperationException("Cannot return a categorical seed from this seed.");
	}
	
	public SampleSeed getContinuous() throws UnsupportedOperationException {
		if (seedType == CONTINUOUS){return this;}
		if (seedType == MIXED) {return (SampleSeed) elements.get(0);}
		throw new UnsupportedOperationException("Cannot return a continuous seed from this seed.");
	}
	
	public T get(int i) {return elements.get(i);}
	public Iterator<T> iterator() {return elements.iterator();}
}
