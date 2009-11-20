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
package stencil.util.streams;

import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

import java.util.Arrays;
import java.util.List;

/**Prototype concurrent loading stream.
 * 
 * All streams are given a round-robing chance to provide data.
 * Since the first stream passed in is given the first chance
 * to load data, it is somewhat dependent on the order.  However,
 * all other streams will be given a chance before that one is
 * given another chance.
 *
 */
public class ConcurrentStream implements TupleStream {
	protected List<TupleStream> streams;

	protected int offset;
	
	public ConcurrentStream(TupleStream... streams) {
		this.streams = Arrays.asList(streams);
	}

	public ConcurrentStream(List<TupleStream> streams) {
		this.streams=streams;
		offset = 0;
	}

	public Tuple next() {
		//TODO: This is busy waiting...we should go with a listener architecture.
		while (!streams.get(offset).ready()) {incrimentOffset();}

		Tuple nv = streams.get(offset).next();
		incrimentOffset();
		return nv;
	}

	public boolean ready() {
		for (TupleStream stream: streams) {
			if (stream.ready()) {return true;}
		}
		return false;
	}

	public boolean hasNext() {
		int initial = offset;
		do {
			if (streams.get(offset).hasNext()) {return true;}	
			incrimentOffset();
		} while (offset != initial);
		
		return false;
	}

	public void remove() {throw new UnsupportedOperationException("Remove not supported on ConcurrentStream.");}
	
	private void incrimentOffset() {offset = (offset+1) % streams.size();}
}
