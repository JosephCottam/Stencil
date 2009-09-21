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
package stencil.adapters.java2D;

import stencil.adapters.java2D.data.*;
import stencil.adapters.java2D.util.DynamicUpdater;
import stencil.adapters.java2D.util.Stopable;
import stencil.display.StencilPanel;
import stencil.parser.tree.Program;
import stencil.parser.tree.Rule;
import stencil.streams.Tuple;
import stencil.types.Converter;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

public class Panel extends StencilPanel<Glyph2D, Table<Glyph2D>, Canvas> {
	List<Stopable> workers = new ArrayList<Stopable>();
	
	public Panel(Program p) {
		super(p, new Canvas(p.getLayers()));
	}
	
	public CanvasTuple getCanvas() {return new CanvasTuple(this.canvas);}
	public ViewTuple getView() {return new ViewTuple(this);}
	public Rectangle getVewBounds() {return canvas.getContentBounds();}
	
	public void dispose() {
		canvas.dispose();
		for (DynamicUpdater updater: updaters.values()) {updater.signalStop();}
	}
	
	public void export(String filename, String type, Object info) throws Exception {
		if (type.equals("PNG") || type.equals("RASTER")) {
			exportPNG(filename, Converter.toInteger(info));
		} else {super.export(filename, type, info);}
	}
	
	private void exportPNG(String filename, Integer dpi) throws Exception { 
		//TODO: DPI Scaling
		ImageIO.write(canvas.buffer, "png", new java.io.File(filename));
	}

	
	//Dynamic updater and updater tracker...
	private Map<Rule, DynamicUpdater> updaters = new HashMap();
	public void addDynamic(Glyph2D g, Rule rule, Tuple source) {
		DynamicUpdater updater;
		Table table = null;
		
		for (Table t: canvas.layers) {if (t.getName().equals(rule.getGroup().getLayer().getName())) {table = t; break;}}
		assert table != null : "Table null after name-based search.";
		
		if (!updaters.containsKey(rule)) {
			updater = new DynamicUpdater(table, rule);
			updaters.put(rule, updater);
			updater.start();
		}
		updater = updaters.get(rule);
		updater.addUpdate(source, g);
	}


	@Override
	public void transfer(Tuple source, Glyph2D target) throws Exception {
		Glyph2D result = target.update(source);
		if (result == source) {return;}
		
		Table t = target.getLayer();
		t.update(result);
	}
}
