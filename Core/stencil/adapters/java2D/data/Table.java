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


 import java.util.Iterator;
import java.util.List;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import stencil.adapters.java2D.data.glyphs.*;
import stencil.display.DisplayGuide;
import stencil.display.DuplicateIDException;
import stencil.parser.tree.Layer;

public class Table<T extends Glyph2D> implements stencil.display.DisplayLayer<T> {
	private ConcurrentMap<String, T> index = new ConcurrentHashMap<String, T>();
	private int generation =0;
	private String name; 
	private T prototypeGlyph;

	protected Table(String name, T prototype) {
		this.name = name;
		this.prototypeGlyph = prototype;
	} 
	
	public String getName() {return name;}
	
	public T find(String ID) {
		return index.get(ID);
	}

	public DisplayGuide getGuide(String attribute)
			throws IllegalArgumentException {
		return null;
	}

	public Iterator<T> iterator() {
		return index.values().iterator();
	}

	public T make(String ID) throws DuplicateIDException {
		T glyph = (T) prototypeGlyph.update("ID", ID);
		glyph.setLayer(this);
		index.put(ID, glyph);
		return glyph;
	}

	public T makeOrFind(String ID) {
		if (index.containsKey(ID)) {return index.get(ID);}
		else {return make(ID);}
	}

	public void remove(String ID) {index.remove(ID);}

	public int size() {return index.size();}
	
	/**Updates an existing glyph with the new glyph.
	 * Checks that the new glyph actually replaces an old one.
	 * @param glyph
	 * @param RuntimeException The glyph introduced does not update a prior glyph (ID matching is used to determine if something is an update)
	 */
	public void update(T glyph) throws RuntimeException {
		String ID = glyph.getID();
		T prior = index.replace(ID, glyph);
		updateGeneration();
		
		glyph.setLayer(this);
		
		if (prior == null) {throw new RuntimeException("Error updating " + ID);}
	}

	/**What is the current edit generation of the data table?
	 * Any edit should update the generation id.  Generations
	 * ids do not have to be assigned sequentially, so that property
	 * should not be relied on, however sufficient generation ids
	 * should be used to keep the repeat rate low.
	 * 
	 * Generation id is not guaranteed to change in any timely manner,
	 * only eventually after an update.
	 */
	public int getGeneration() {return generation;}
	
	/**Update the generation ID.**/ 
	private void updateGeneration() {generation++;}
	
	public List<String> getPrototype() {return prototypeGlyph.getFields();}
	
	public static Table<?> instance(Layer l) {
		String name = l.getName();
		String implantation = l.getImplantation();
		
		try {
			if (implantation.equals("SHAPE")) {
				return new Table<Shape>(name, new Shape("PROTOTYPE"));
			} else if (implantation.equals("LINE")) {
				return new Table<Line>(name, new Line("PROTOTYPE"));
			} else if (implantation.equals("TEXT")) {
				return new Table<Text>(name, new Text("PROTOTYPE"));
			} else if (implantation.equals("PIE")) {
				return new Table<Pie>(name, new Pie("PROTOTYPE"));
			} else if (implantation.equals("IMAGE")) {
				return new Table<Image>(name, new Image("PROTOTYPE"));
			}

		} catch (Throwable e) {throw new RuntimeException("Error instantiating table for implantation: " + implantation, e);}
		throw new IllegalArgumentException("Glyph type not know: " + implantation);
	}

}
