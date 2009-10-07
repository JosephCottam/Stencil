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
import stencil.display.StencilPanel;
import stencil.interpreter.DynamicRule;
import stencil.parser.tree.Program;
import stencil.parser.tree.Rule;
import stencil.streams.Tuple;
import stencil.types.Converter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

public class Panel extends StencilPanel<Glyph2D, DisplayLayer<Glyph2D>, Canvas> {
	public Panel(Program p) {
		super(p, new Canvas(p.getLayers()));
	} 
	
	/**Listening to the panel is really listening to its canvas.
	 * HACK: How can you get events to bubble up automatically?  Do I need to use glass pane?
	 */
	public synchronized void addMouseListener(MouseListener l) {canvas.addMouseListener(l);}
	public synchronized void addMouseMotionListener(MouseMotionListener l) {canvas.addMouseMotionListener(l);}
	
	public CanvasTuple getCanvas() {return new CanvasTuple(this.canvas);}
	public ViewTuple getView() {return new ViewTuple(this);}
	
	@SuppressWarnings("deprecation")
	public void dispose() {
		canvas.dispose();
		for (DynamicUpdater updater: updaters.values()) {updater.signalStop();}
		
		//Ensure threads stop
		for (Thread t:updaterThreads) {
			try {t.join(10000);}
			catch (Exception e) {t.stop();}
		}
	}
	
	public void export(String filename, String type, Object info) throws Exception {
		if (type.equals("PNG") || type.equals("RASTER")) {
			exportPNG(filename, Converter.toInteger(info));
		} else {super.export(filename, type, info);}
	}
	
	private void exportPNG(String filename, Integer dpi) throws Exception { 
		double scale;

		try {scale =Math.ceil(((double) dpi)/java.awt.Toolkit.getDefaultToolkit().getScreenResolution());}
		catch (java.awt.HeadlessException e) {scale = Math.ceil(((double) dpi)/StencilPanel.ABSTRACT_SCREEN_RESOLUTION);}

		long width = (int) Math.round(Math.ceil(scale * canvas.getWidth()));
		long height = (int) Math.round(Math.ceil(scale * canvas.getHeight()));

		AffineTransform viewTransform = canvas.getViewTransformRef(); 
		Point2D topLeft = viewTransform.transform(canvas.getBounds().getLocation(), null);
		
		if (width == 0 || height == 0) { //If nothing will display, then just show everything (usually batch mode)
			Rectangle viewBounds = canvas.getContentBounds();
			width = (int) Math.round(Math.ceil(scale * viewBounds.width));
			height = (int) Math.round(Math.ceil(scale * viewBounds.height));
			topLeft = canvas.getContentBounds().getLocation();
			topLeft = new Point2D.Double(-topLeft.getX(), -topLeft.getY());
		}
		
		if (width ==0 || height ==0) {throw new RuntimeException("Cannot export a zero-sized image.");}
		double prop = width/height;

		//Calculate trimmed dimension
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
			height = (int) Math.floor(Math.round(width * prop));
		}

		if (height > Integer.MAX_VALUE) {
			height = Integer.MAX_VALUE;
			width = (int) Math.floor(Math.round(width / prop));
		}
		
		BufferedImage buffer = (BufferedImage) canvas.createImage((int) width, (int) height);
		if (buffer == null) { //Happens in headless mode
			buffer = new BufferedImage((int) width,  (int) height, BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics2D g = buffer.createGraphics();
		g.setPaint(canvas.getBackground());
		g.fill(new Rectangle(0,0, (int) width, (int) height));

		AffineTransform exportViewTransform = AffineTransform.getTranslateInstance(scale* topLeft.getX(), scale*topLeft.getY());
		exportViewTransform.scale(scale * viewTransform.getScaleX(), scale* viewTransform.getScaleY());
//		AffineTransform exportViewTransform = AffineTransform.getScaleInstance(scale * viewTransform.getScaleX(), scale* viewTransform.getScaleY());
//		exportViewTransform.translate(topLeft.getX(), topLeft.getY());
		g.transform(exportViewTransform);
		
		canvas.painter.doDrawing(g, exportViewTransform);
		ImageIO.write(buffer, "png", new java.io.File(filename));
	}

	
	//Dynamic updater and updater tracker...
	private final Map<Rule, DynamicUpdater> updaters = new HashMap();
	private final List<Thread> updaterThreads = new ArrayList();
	public void addDynamic(Glyph2D g, Rule rule, Tuple source) {
		DynamicUpdater updater;
		DisplayLayer table = null;
		
		for (DisplayLayer t: canvas.layers) {if (t.getName().equals(rule.getGroup().getLayer().getName())) {table = t; break;}}
		assert table != null : "Table null after name-based search.";
		
		if (!updaters.containsKey(rule)) {
			Rule dynamicRule = DynamicRule.toDynamic(rule);
			updater = new DynamicUpdater(table, dynamicRule);
			updaters.put(rule, updater);
			Thread thread = new Thread(updater);
			updaterThreads.add(thread);
			thread.start();
		}

		updater = updaters.get(rule);
		updater.addUpdate(source, g);
	}


	@Override
	public void transfer(Tuple source, Glyph2D target) throws Exception {
		Glyph2D result = target.update(source);
		if (result == source) {return;}
		
		DisplayLayer t = target.getLayer();
		t.update(result);
	}
}
