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
package stencil.explore.coordination;

import java.util.List;

import stencil.explore.model.AdapterOpts;
import stencil.explore.model.sources.StreamSource;


/**Event indicating that a stencil object was changed was changed.*/
public class StencilEvent<T> extends java.util.EventObject {
	public static enum Type {Stencil, Sources, Config, All}

	/**Event to indicate the configuration changed.
	 * This includes compiler/interpreter configuration or application information.
	 */
	public static class ConfigChanged extends StencilEvent<AdapterOpts> {
		public ConfigChanged(Object source, AdapterOpts opts) {super(source, Type.Config, opts);}
	}

	/**Event to indicate the actual stencil code changed.*/
	public static class StencilChanged extends StencilEvent<String> {
		public StencilChanged(Object source, String value) {super(source, Type.Stencil, value);}
	}

	/**Event to indicate that the stencil sources have changed.
	 * This can indicate that a source has been either added, removed or edited.
	 */
	public static class SourcesChanged extends StencilEvent<List<StreamSource>> {
		public SourcesChanged(Object source, List<StreamSource> streamSource) {super(source, Type.Sources, streamSource);}
	}

	protected Type type;
	protected T value;

	public StencilEvent(Object source, Type type) {this(source, type, null);}
	public StencilEvent(Object source, Type type, T value) {
		super(source);
		this.type = type;
		this.value = value;
	}

	public T getValue() {return value;}
}
