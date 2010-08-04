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
package stencil.parser.tree;

import java.util.Arrays;

import org.antlr.runtime.Token; 

import stencil.types.Converter;

public class StateQuery extends StencilTree {
	private static final Object[] EMPTY_ARGS = new Object[0];
	private int[] cachedIDs;
	
	public StateQuery(Token source) {
		super(source);
	}
	
	/**Get a StateID that is the composite of all current stateIDs.
	 * (This will calculate new stateIDs, but not store them in the cache.)
	 */
	public int compositeStateID() {
		final Integer[] nowIDs = new Integer[getChildCount()];
		for (int i=0; i< nowIDs.length; i++) {
			AstInvokeable inv = (AstInvokeable) getChild(i);
			nowIDs[i] = Converter.toInteger(inv.invoke(EMPTY_ARGS).get(0));
		}
		return Arrays.deepHashCode(nowIDs);
	}
	
	public int[] getStateIDs() {return cachedIDs;}
	
	/**Has any of the contained stateID queries changed?*/
	public boolean requiresUpdate() {
		final int[] nowIDs = new int[getChildCount()];
		if (cachedIDs == null) {
			cachedIDs = new int[nowIDs.length];
			Arrays.fill(cachedIDs, Integer.MAX_VALUE-1);
		}
		
		for (int i=0; i< nowIDs.length; i++) {
			AstInvokeable inv = (AstInvokeable) getChild(i);
			nowIDs[i] = Converter.toInteger(inv.directInvoke(EMPTY_ARGS));
		}		
				
		boolean matches = true;	//Do the two ID arrays match?
		for (int i=0; matches && i < nowIDs.length; i++) {
			matches = matches && (nowIDs[i] == cachedIDs[i]);
		}
				
		cachedIDs = nowIDs;
		return !matches;
	}
	
	public StateQuery dupNode() {
		StateQuery dup = (StateQuery) super.dupNode();
		dup.cachedIDs = this.cachedIDs;
		return dup;
	}
}
