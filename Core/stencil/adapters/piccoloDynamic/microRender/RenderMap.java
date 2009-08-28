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

import java.util.HashMap;

import stencil.adapters.piccoloDynamic.glyphs.Node;

/**Utility to coordinate attributes, proxy value storage and their associated
 * micro-renderers.  If this object is employed by a node, all attributes handled
 * by it can be updated in the pre-paint method by including
 * "apply(this)" in the method body.
 * @author jcottam
 *
 * @param <T>
 */
public class RenderMap<T extends Node> extends HashMap<String, MicroRenderer> {
	public static final String PROXY_PREFIX = "SteincilProxy_";

	/**Apply any micro-renderers to the given target.
	 * No short-circuit for no-update rendering is used.
	 *
	 * TODO: Short circuit logic.
	 *
	 * @param target
	 */
	public void apply(T target) {
		for(String att: this.keySet()) {
			MicroRenderer renderer = this.get(att);
			String proxyName = proxyName(att);
			Object proxy = target.getAttribute(proxyName);
			Object value = renderer.reify(proxy);
			target.setAttribute(att, value);
		}
	}

	/**Given an attribute name, where will the proxy value be stored? */
	public static String proxyName(String attribute) {return PROXY_PREFIX + attribute;}
}
