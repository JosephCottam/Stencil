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
import stencil.adapters.java2D.util.GuideUpdater;
import stencil.display.StencilPanel;
import stencil.parser.tree.Program;
import stencil.parser.tree.Rule;
import stencil.tuple.Tuple;
import stencil.types.Converter;
import stencil.util.epsExport.EpsGraphics2D;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Panel extends StencilPanel<Glyph2D, DisplayLayer<Glyph2D>, Canvas> {
	private final DynamicUpdater dynamicUpdater = new DynamicUpdater();
	private Thread dynamicUpdaterThread = new Thread(dynamicUpdater);
	
	private final GuideUpdater guideUpdater;
	private final Thread guideUpdaterThread;
	
	public Panel(Program p) {
		super(p, new Canvas(p.getCanvasDef(), p.getLayers()));
		
		 GuideUpdater updater = new GuideUpdater(p, this);
		 if (updater.required()) {
			 guideUpdater = updater;
			 guideUpdaterThread = new Thread(guideUpdater);
			 guideUpdaterThread.start();
		 } else {
			 guideUpdater = null;
			 guideUpdaterThread = null;
		 }
	} 
	
	/**Listening to the panel is really listening to its canvas.
	 * HACK: Combine panel and canvas when Piccolo goes away...
	 */
	public synchronized void addMouseListener(MouseListener l) {canvas.addMouseListener(l);}
	public synchronized void addMouseMotionListener(MouseMotionListener l) {canvas.addMouseMotionListener(l);}
	
	public CanvasTuple getCanvas() {return new CanvasTuple(this.canvas);}
	public ViewTuple getView() {return new ViewTuple(this);}
	
	@SuppressWarnings("deprecation")
	public void dispose() {
		dynamicUpdater.signalStop();
		try {dynamicUpdaterThread.join(10000);}
		catch (Exception e) {dynamicUpdaterThread.stop();}

		if (guideUpdater !=null) {
			guideUpdater.signalStop();
			try {guideUpdaterThread.join(10000);}
			catch (Exception e) {guideUpdaterThread.stop();}
		}
		
		canvas.dispose();		
	}
	
	public void export(String filename, String type, Object info) throws Exception {
		prepForExport();

		if (type.equals("PNG") || type.equals("RASTER")) {
			exportPNG(filename, Converter.toInteger(info));
		} else if (type.equals("EPS") || type.equals("VECTOR") && info == null) {
			exportEPS(filename);
		} else if (type.equals("EPS") || type.equals("VECTOR")) {
			exportEPS(filename, (Rectangle) info);
		} else {super.export(filename, type, info);}
	}
	
		
	/**Prep for export by running the guides and dynamic rules.
	 * 
	 * TODO: Dynamic rules need to be given a chance to quiesce, not just be run once....
	 * */
	private void prepForExport() {
		if (guideUpdater != null) {guideUpdater.runOnce();}
		if (dynamicUpdater != null) {dynamicUpdater.runOnce();}
	}
	
	
	private void exportEPS(String filename, Rectangle bounds) throws Exception {
		File f= new File(filename);
		EpsGraphics2D g = new EpsGraphics2D("Stencil Output", f, bounds.x, bounds.y, bounds.x+ bounds.width, bounds.y+ bounds.height);
		canvas.painter.doDrawing(g);
		g.close();
		g.dispose();		
		
	}
	private void exportEPS(String filename) throws Exception {
		exportEPS(filename, canvas.getContentBounds());
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
		Rectangle bounds = new Rectangle(0,0, (int) width, (int) height); 
		g.fill(bounds);

		AffineTransform exportViewTransform = AffineTransform.getTranslateInstance(scale* topLeft.getX(), scale*topLeft.getY());
		exportViewTransform.scale(scale * viewTransform.getScaleX(), scale* viewTransform.getScaleY());
		g.transform(exportViewTransform);
		
		canvas.painter.doDrawing(g);
		ImageIO.write(buffer, "png", new java.io.File(filename));
	}

	
	public void addDynamic(Glyph2D glyph, Rule rule, Tuple source) {
		DisplayLayer layer= null;
		for (DisplayLayer t: canvas.layers) {if (t.getName().equals(rule.getGroup().getLayer().getName())) {layer = t; break;}}
		assert layer != null : "Table null after name-based search.";
		
		dynamicUpdater.addDynamicUpdate(glyph, rule, source, layer);
		
		if (!dynamicUpdaterThread.isAlive()) {
			try {dynamicUpdaterThread.start();}
			catch (IllegalThreadStateException e) {
				/*Ignored. Happens if an attempt is made to
				 * start a thread after it has been started once.
				 * Multi-threading loading can incur multiple starts, the subsequent ones are ignored.
				 */				
			}
		}
	}


	public Glyph2D transfer(Tuple source, Glyph2D target) throws Exception {
		Glyph2D result = target.update(source);
		if (result == target) {return target;}
		
		DisplayLayer t = target.getLayer();
		t.update(result);
		return result;
	}
	
	public Rectangle getInsetBounds() {
		if (this.getBorder() == null) {return this.getBounds();}
		
		Rectangle bounds = this.getBounds();
		Insets insets = this.getInsets();
		
		return new Rectangle(bounds.x-insets.left, 
							bounds.y-insets.top,
							bounds.width-(insets.left+insets.right),
							bounds.height-(insets.top+insets.bottom));
	}

}
