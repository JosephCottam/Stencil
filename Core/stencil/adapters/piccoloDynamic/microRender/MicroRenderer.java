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
package stencil.adapters.piccoloDynamic.microRender;

/**
 * A MicroRenderer defines a pair of mappings between a source
 * value and two targets: proxy and reified.
 *
 * @param <Source> Type of the objects to expect as inputs to proxy
 * @param <Proxy> Type of values returned as proxies
 * @param <Target> Type of values returned from reify
 **/
public interface MicroRenderer<Source,Proxy,Target> {

	/**Make a concrete object into an abstract reference.  Returns
	 * a proxy for the original value that may be used to retrieve
	 * that value later through the unproxy method.  The value returned
	 * by this method is an abstraction of the original.  It should
	 * not be considered equivalent to that value (as it may very well
	 * have distinct properties) but it is congruent through the micro-renderer
	 * to that original value (in the same way that 3 is congruent to 9
	 * through modulo 6).  That being said, this method may return the original
	 * value as its proxy (this would satisfy the conditions of proxy, but
	 * it is not the common case and should not be relied on in the general case).
	 *
	 * @param value
	 * @return
	 */
	public Proxy proxy(Source value);


	/**Map a proxy back to its original value.  This is the reflection of
	 * the proxy operation.  x.equals(unproxy(proxy(x))) for all x where
	 * equals is not strictly specified as ==.  This method may return the
	 * original value passed to proxy, or it may return a reconstruction of
	 * that based on the proxy.
	 *
	 * @param value
	 * @return
	 */
	public Source unproxy(Proxy value);


	/**Turn a proxy in to a representative.   This is like unproxy in that
	 * it returns a value suitable to be used outside of the MicroRenderer,
	 * but unlike unproxy, this method may return an object distinct from
	 * the original input in both value and type.
	 *
	 * This new object should have the following properties:
	 *
	 * 1)  reify(x) == reify(y) IF x.equals(y)
	 * 2)  reify(x) may equal reify(y) when !x.equals(y)
	 *
	 * Reification may take into account information not available
	 * at the time the proxy was created.
	 *
	 * @param proxy
	 * @return
	 */
	public Target reify(Proxy proxy);
}
