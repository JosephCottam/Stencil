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

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import stencil.display.StencilPanel;
import stencil.interpreter.DynamicRule;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Program;
import stencil.parser.tree.Rule;
import stencil.streams.Tuple;
import stencil.util.Tuples;
import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.adapters.piccoloDynamic.util.PiccoloGlyph;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PBounds;

public class Panel extends StencilPanel<PiccoloGlyph, DisplayLayer, PCanvas> {
	private static final long serialVersionUID = 1L;
	public Panel(Program program) {
		super(program, new PCanvas());

		for (Layer layer: program.getLayers()) {
			canvas.getCamera().addLayer(((DisplayLayer)layer.getDisplayLayer()).getSource());
		}
		
		if (DEFAULT_INTERACTION) {
			canvas.addInputEventListener(new ExtendedZoomHandler());
		}else {
			canvas.setZoomEventHandler(null);
			canvas.setPanEventHandler(null);
		}
	}

	/**Listening to the panel is really listening to its canvas.
	 * HACK: How can you get events to bubble up automatically?  Do I need to use glass pane?
	 */
	public void addMouseListener(MouseListener l) {canvas.addMouseListener(l);}
	public void addMouseMotionListener(MouseMotionListener l) {canvas.addMouseMotionListener(l);}
	
	public CanvasTuple getCanvas() {return new CanvasTuple(canvas);}
	public ViewTuple getView() {return new ViewTuple(canvas.getCamera());}

	public String[] getExports() {
		return new String[]{"PNG", "TXT"};
	}
	
	/**Exports a graphic of the current panel.
	 *
	 * Types values supported:
	 *		RASTER 	-- Export as a png.  Info must be an Integer that specifies DPI.
	 * 		VECTOR 	-- Export as an eps.  Info is ignored.
	 * 		TUPLES 	-- Export a list of tuples specifying the entire object
	 * 		PNG		-- Same as RASTER
	 * 		EPS		-- Same as VECTOR
	 * 		TXT 	-- Same as TUPLES
	 */
	public void export(String filename, String type, Object info) throws Exception {
		type = type.toUpperCase();
		if (type.equals("PNG") || type.equals("RASTER")) {savePNG(filename, (Integer) info);}
		else {super.export(filename, type, info);}
	}

	/**Exports the display as currently constituted to an PNG having at least the given DPI.
	 * 
	 * An integral multiplier to the screen resolution is used to retain crispness. 
	 * The multiplier is selected so the resulting DPI is at least that specified, and minimally
	 * greater than it to retain an integral value.  
	 * 
	 * */
	private void savePNG(String filename, int dpi) throws Exception {
		double scale;

		try {scale =Math.ceil(dpi/java.awt.Toolkit.getDefaultToolkit().getScreenResolution());}
		catch (java.awt.HeadlessException e) {scale = Math.ceil(((double) dpi)/StencilPanel.ABSTRACT_SCREEN_RESOLUTION);}

		long width =  Math.round(Math.ceil(scale * this.getWidth()));
		long height =  Math.round(Math.ceil(scale * this.getHeight()));

		if (width == 0 || height == 0) { //If nothing will display, then just show everything (usually batch mode)
			CanvasTuple c = this.getCanvas();
			PBounds bounds = new PBounds(c.getDouble("X"), c.getDouble("Y"), c.getDouble("WIDTH"), c.getDouble("HEIGHT"));

			width = (int) Math.round(Math.ceil(scale * bounds.width));
			height = (int) Math.round(Math.ceil(scale * bounds.height));

			canvas.setBounds(bounds.getBounds());							//set the window to the right size
			canvas.getCamera().animateViewToCenterBounds(bounds,true,0); 	//Set the view to the right place
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


		BufferedImage b = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		canvas.getCamera().toImage(b, java.awt.Color.WHITE);
		ImageIO.write(b, "png", new java.io.File(filename));
	}

	@Override
	public void addDynamic(PiccoloGlyph glyph, Rule rule, Tuple source) {
		assert glyph instanceof NodeTuple : "Can only use NodeTuple glyphs";
	
		NodeTuple node = (NodeTuple) glyph;
		Rule dynamicRule = DynamicRule.toDynamic(rule);
		Node.DynamicRule dynamic = new Node.DynamicRule(source, dynamicRule, this);
		node.getNode().dynamicRules.add(dynamic);
	}


	private boolean transfers = true;
	private Object transferLock = "Lock";
	
	
	public void setTransfers() {synchronized(transferLock) {this.transfers = true;}}
	
	boolean hasUpdates() {
		synchronized (transferLock) {
			boolean transfers = this.transfers;
			this.transfers = false;
			return transfers;
		}
	}
	

	/**Perform a transfer operation in the event dispatch thread.*/
	public void transfer(final Tuple source, final PiccoloGlyph target) throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			Tuples.transfer(source, target, false);
		} else {
			final Runnable r = new Runnable() {
				public final void run() {
					Tuples.transfer(source, target, false);
				}
			};
			SwingUtilities.invokeAndWait(r);
		}
	}
}
