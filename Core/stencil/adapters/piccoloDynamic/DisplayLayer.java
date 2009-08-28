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
package stencil.adapters.piccoloDynamic;

import static stencil.adapters.GlyphAttributes.*;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;

import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.adapters.piccoloDynamic.util.ImplantationCache;
import stencil.adapters.piccoloDynamic.util.ZPLayer;
import stencil.display.DisplayGuide;
import stencil.display.DuplicateIDException;
import edu.umd.cs.piccolo.*;


public class DisplayLayer implements stencil.display.DisplayLayer<NodeTuple> {
	private final Object LAYER_LOCK = ""; //Special lock to make sure index and layer children stay in synch

	protected ZPLayer source;
	protected String name;
	protected String glyphType;
	protected List<String> prototype;
	protected Map<String, Node> index = new HashMap<String, Node>();
	protected Map<String, DisplayGuide> guides = new HashMap<String, DisplayGuide>();

	public DisplayLayer(ZPLayer source, String name, String implantation) {
		if (source == null) {throw new IllegalArgumentException("Source layer cannot be null");}
		if (name == null) {throw new IllegalArgumentException("Name cannot be null.");}

		this.source = source;
		this.name = name;
		setImplantation(implantation);
	}

	public NodeTuple<? extends Node> makeOrFind(String ID) {
		if (index.containsKey(ID)) {return new NodeTuple(index.get(ID));}
		return make(ID);
	}

	public NodeTuple<? extends Node> find(String ID) {
		if (index.containsKey(ID)) {return new NodeTuple(index.get(ID));}
		return null;
	}

	/**Create a new item in this layer to represent the given ID*/
	public NodeTuple make(String id) {
		NodeTuple value = new NodeTuple(ImplantationCache.instance.instance(glyphType, id));		
		register(value);
		return value;
	}

	private void register(final NodeTuple value) {
		String ID = (String) value.get(StandardAttribute.ID.name(), String.class);
		if (index.containsKey(ID)) {throw new DuplicateIDException(ID, name);}

		synchronized (this.LAYER_LOCK) {
			index.put(ID, value.node);
			addChild(value.node, ID);
		}
		value.set(StandardAttribute.LAYERNAME.name(), name); //Make sure the layer can be found in the node...
	}

	public void remove(final String ID){
		synchronized (this.LAYER_LOCK) {
			final PNode node = index.remove(ID);
			final Runnable r = new Runnable() {public final void run() {source.removeChild(node);}};

			try {
				if (SwingUtilities.isEventDispatchThread()) {r.run();}
				else {SwingUtilities.invokeAndWait(r);}
			} catch (Exception e) {
				throw new RuntimeException("Error removing object " + ID + " from layer " + name + ".", e);
			}
		}
	}

	public String getName() {return name;}
	public int size() {return index.size();}

	public PLayer getSource() {return source;}

	/**Iterator over all of the keys currently held by this layer.  By iterating the keys,
	 * the tuples of the layer may also be iterated.
	 */
	public Iterator<NodeTuple> iterator() {
		return new Iterator<NodeTuple>() {
			Iterator<Node> source = index.values().iterator();

			public boolean hasNext() {
				return source.hasNext();
			}

			public NodeTuple next() { 
				return new NodeTuple(source.next());
			}

			public void remove() {throw new UnsupportedOperationException();}			
		};
	}
	
	public List<String> getPrototype() {return prototype;}
	
	private void setImplantation(String name) {
		this.glyphType = name;
		prototype = new NodeTuple(ImplantationCache.instance.instance(name, "junk")).getFields();
	}

	public boolean hasGuide(String attribute) {return guides.containsKey(attribute);}
	
	public DisplayGuide getGuide(String attribute)
			throws IllegalArgumentException {
		if (!hasGuide(attribute)) {throw new IllegalArgumentException(String.format("Error retrieving guide for %1$s on %2$s.", attribute, getName()));}
		return guides.get(attribute);
	}
	
	public void addGuide(String attribute, DisplayGuide guide) {
		if (guide == null) {throw new IllegalArgumentException(String.format("Cannot have a null-valued guide (requested for %1$s on %2$s).", attribute, getName()));}
		guides.put(attribute, guide);
		synchronized (this.LAYER_LOCK) {
			addChild((PNode) guide, "Guide: " + attribute);
		}
	}
	
	private void addChild(final PNode node, String ID) {
		try {
			final Runnable r = new Runnable() {public final void run() {source.addChild(node);}};
			if (SwingUtilities.isEventDispatchThread()) {r.run();}
			else {SwingUtilities.invokeAndWait(r);}
		} catch (Exception e) {throw new RuntimeException(String.format("Error registriong %1$s on layer %2$s", ID, name), e);}
	}
}
