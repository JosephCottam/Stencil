/** Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
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
package stencil.adapters.piccoloDynamic;

import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

import stencil.adapters.Glyph;
import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.types.Converter;
import stencil.util.Tuples;
import stencil.util.enums.EnumUtils;

import static stencil.adapters.GlyphAttributes.*;

public class NodeTuple<T extends Node> implements Glyph {
	protected T node;

	/**Create a new PNode tuple from the given source.  The object passed
	 * must be a PNode that implements both AttributePNode and ZPNode,
	 * all other objects will result in an IllegalArgumentException.
	 * @param source
	 */
	public NodeTuple(T source) {
		if (source == null) {throw new IllegalArgumentException("Cannot specify null tree for PNodeTuple.");}
		node = source;
	}


	public Object get(String name, Class<?> type) {
		Object value = get(name);
		return Converter.convert(value, type);
	}

	public Object get(String name) {return node.getAttribute(name);}

	public String getSource() {return ((String) node.getAttribute(StandardAttribute.LAYERNAME));}

	public void set(final String name, final Object value) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				node.setAttribute(name, value);
			} else {
				final Runnable r =new Runnable() {
					public final void run() {node.setAttribute(name, value);}
				};
				SwingUtilities.invokeAndWait(r);
			}
		} catch (Exception e) {throw new Error("Error updating node.",e);}
	}

	public List<String> getFields() {return new ArrayList(node.getAttributes());}

	public boolean hasField(String name) {
		name = Node.baseName(name);
		if (EnumUtils.contains(StandardAttribute.class, name)) {return true;}
		return node.getAttributes().contains(name);
	}

	public T getNode() {return node;}
	public String toString() {return Tuples.toString(this);}

	public boolean isDefault(String name, Object value) {
		Object def = node.getDefault(name);

		if (def == null) {return value == null;}
		return def.equals(value);
	}

}
