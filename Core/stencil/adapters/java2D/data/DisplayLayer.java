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
package stencil.adapters.java2D.data;
 
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import stencil.adapters.java2D.data.glyphs.*;
import stencil.adapters.java2D.util.LayerUpdateListener;
import stencil.display.DisplayGuide;
import stencil.display.DuplicateIDException;
import stencil.parser.ParserConstants;
import stencil.parser.tree.Layer;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.collections.ListSet;

public final class DisplayLayer<T extends Glyph2D> implements stencil.display.DisplayLayer<T> {
	private ConcurrentMap<String, T> index = new ConcurrentHashMap<String, T>();
	private final String name; 
	private T prototypeGlyph;
	private final Map<String, Guide2D> guides  = new ConcurrentHashMap<String, Guide2D>();
	private final Set<LayerUpdateListener> updateListeners = new ListSet();	

	protected DisplayLayer(String name) {this.name = name;}
	
	public String getName() {return name;}

	public Iterator<T> iterator() {return index.values().iterator();}
	
	public T find(String ID) {return index.get(ID);}

	public T makeOrFind(Tuple values) {
		String id = Converter.toString(values.get(ParserConstants.GLYPH_ID_FIELD));
		T rv;

		if (index.containsKey(id)) {
			T old = index.get(id);
			rv = old;
			if (values.getPrototype().size()>1) {
				rv = (T) rv.update(values);
				index.put(id,rv);
				fireLayerUpdate(old.getBoundsReference(), rv.getBoundsReference());
			}
		} else {rv = make(values);}
		
		return rv;
	}

	public T make(Tuple values) throws DuplicateIDException {
		String id = Converter.toString(values.get(ParserConstants.GLYPH_ID_FIELD));
		T glyph = (T) prototypeGlyph.update(values);
		index.put(id, glyph);
		fireLayerUpdate(glyph.getBoundsReference());
		return glyph;
	}

	public void remove(String ID) {
		T glyph = index.remove(ID);
		if (glyph != null) {fireLayerUpdate(glyph.getBoundsReference());}
	}

	public int size() {return index.size();}
	
	/**Updates an existing glyph with the new glyph.
	 * Checks that the new glyph actually replaces an old one.
	 * 
	 * @param glyph
	 * @param RuntimeException The glyph introduced does not update a prior glyph (ID matching is used to determine if something is an update)
	 */
	public void update(T glyph) throws RuntimeException {
		String ID = glyph.getID();
		T prior = index.replace(ID, glyph);
		fireLayerUpdate(prior.getBoundsReference(), glyph.getBoundsReference());
		
		if (prior == null) {throw new RuntimeException("Error updating " + ID);}
	}
	
	private void setPrototype(T prototypeGlyph) {this.prototypeGlyph = prototypeGlyph;}

	/**Get the tuple prototype of this table.*/
	public List<String> getPrototype() {return prototypeGlyph.getPrototype();}
	
	public DisplayGuide getGuide(String attribute) {return guides.get(attribute);}
	public void addGuide(String attribute, Guide2D guide) {guides.put(attribute, guide);}
	public boolean hasGuide(String attribute) {return guides.containsKey(attribute);}
	public Collection<Guide2D> getGuides() {return guides.values();}

	
	public static DisplayLayer<?> instance(Layer l) {
		String name = l.getName();
		String implantation = l.getImplantation();
		DisplayLayer layer = null;
		try {
			if (implantation.equals("SHAPE")) {
				layer =  new DisplayLayer(name);
				layer.setPrototype(new Shape(layer, "PROTOTYPE"));
			} else if (implantation.equals("LINE")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Line(layer, "PROTOTYPE"));
			} else if (implantation.equals("TEXT")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Text(layer, "PROTOTYPE"));
			} else if (implantation.equals("PIE")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Pie(layer, "PROTOTYPE"));
			} else if (implantation.equals("IMAGE")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Image(layer, "PROTOTYPE"));
			} else if (implantation.equals("POLY_LINE")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Poly.PolyLine(layer, "PROTOTYPE"));
			} else if (implantation.equals("POLYGON")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Poly.Polygon(layer, "PROTOTYPE"));
			} else if (implantation.equals("ARC")) {
				layer = new DisplayLayer(name);
				layer.setPrototype(new Arc(layer, "PROTOTYPE"));
			} 
		} catch (Throwable e) {throw new RuntimeException("Error instantiating table for implantation: " + implantation, e);}
		
		if (layer == null) {throw new IllegalArgumentException("Glyph type not know: " + implantation);}

		return layer;
	}
	
	public void addLayerUpdateListener(LayerUpdateListener l) {updateListeners.add(l);}
	
	private void fireLayerUpdate(Rectangle2D pre, Rectangle2D post) {
		Rectangle2D bounds = new Rectangle2D.Double();
		Rectangle2D.union(pre, post, bounds);
		fireLayerUpdate(bounds);
	}
	
	private void fireLayerUpdate(Rectangle2D bounds) {
		Rectangle update = bounds.getBounds();
		for (LayerUpdateListener l:updateListeners) {l.layerUpdated(update);}
	}
}
