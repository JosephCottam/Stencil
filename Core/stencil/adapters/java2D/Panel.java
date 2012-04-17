package stencil.adapters.java2D;

import stencil.display.DisplayLayer;
import stencil.adapters.java2D.columnStore.util.StoreTuple;
import stencil.adapters.java2D.interaction.CanvasTuple;
import stencil.adapters.java2D.interaction.ViewTuple;
import stencil.adapters.java2D.util.MultiThreadPainter;
import stencil.adapters.java2D.util.PainterThread;
import stencil.display.StencilPanel;
import stencil.interpreter.tree.Program;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Map;

import javax.imageio.ImageIO;

public class Panel extends StencilPanel<StoreTuple, DisplayLayer<StoreTuple>, Canvas> {
	private PainterThread painter;
	private Thread painterThread;	//Thread for painting after loading is complete
	
	/**When the canvas and the program are ready, this method finishes constructing the panel.
	 * A panel cannot be used prior to this method being called.
	 */
	protected void init(Canvas canvas, Program program) {
		setProgram(program);
		setCanvas(canvas);
		
		painter = new PainterThread(this);
		painterThread = new Thread(painter, "Painter");
	}

	@Override
	public Map<String, TupleStream> preRun() {
		if (continuousPainting) {painterThread.start();}
		return super.preRun();
	}
	
	@Override
	public void signalStop() {painter.signalStop();}
	
	@Override
	@SuppressWarnings("deprecation")
	public void dispose() {
		if (painterThread != null && painterThread.isAlive()) {
			painter.signalStop();
			try {
				painterThread.join(2000);
				if (painterThread.isAlive()) {
					painterThread.stop();
				}
			} catch (Exception e) {
				System.err.println("Error shutingdown painter thread ingored (reported below).");
				e.printStackTrace();
			}
		}
	}
	
	
	/**Listening to the panel is really listening to its canvas.
	 * HACK: Combine panel and canvas when Piccolo goes away...
	 */
	@Override
	public synchronized void addMouseListener(MouseListener l) {canvas.addMouseListener(l);}
	@Override
	public synchronized void addMouseMotionListener(MouseMotionListener l) {canvas.addMouseMotionListener(l);}
	
	@Override
	public CanvasTuple getCanvas() {return new CanvasTuple(this.canvas);}
	@Override
	public ViewTuple getView() {return new ViewTuple(this);}
	
	@Override
	public void export(String filename, String type, Object info) throws Exception {
		if (type.equals("PNG") || type.equals("RASTER")) {
			exportPNG(filename, Converter.toInteger(Array.get(info,0)));
		} else if (type.equals("PNG2")) {
			exportPNG(filename, Converter.toInteger(Array.get(info,0)), Converter.toInteger(Array.get(info, 1)));
		} else {super.export(filename, type, info);}
	}
	

	//TODO: Does not work if there is a #RENDER consumption on a view....
	private void exportPNG(String filename, Integer width, Integer height) throws Exception {
		if (width <1 && height <1) {exportPNG(filename, StencilPanel.ABSTRACT_SCREEN_RESOLUTION); return;}
		Rectangle contentBounds = canvas.contentBounds(true);
		
		AffineTransform viewTransform = canvas.viewTransformRef(); 
		Point2D topLeft = viewTransform.transform(canvas.getBounds().getLocation(), null);		
		
		double contentWidth = canvas.getWidth();		//Using double to force floating point arithmetic later
		double contentHeight = canvas.getHeight();
		if (contentWidth == 0 || contentHeight == 0) { //If nothing will display, then just show everything (happens in batch mode)
			contentWidth = contentBounds.getWidth();
			contentHeight = contentBounds.getHeight();
			topLeft = contentBounds.getLocation();
			topLeft = new Point2D.Double(-topLeft.getX(), -topLeft.getY());
		}

		
		if (width <1)  {width  = (int) (contentWidth  * (height / contentHeight));}
		if (height <1) {height = (int) (contentHeight * (width  / contentWidth));}
		if (width <1)  {width = 1;}
		if (height <1) {height =1;}
		double xScale = width/contentWidth;
		double yScale = height/contentHeight;
		double scale = Math.max(xScale, yScale);

		BufferedImage buffer = (BufferedImage) canvas.createImage(width, height);
		if (buffer == null) { //Happens in headless mode
			buffer = new BufferedImage(width,  height, BufferedImage.TYPE_INT_ARGB);
		}		
		
		AffineTransform exportViewTransform = AffineTransform.getTranslateInstance(scale* topLeft.getX(), scale*topLeft.getY());
		exportViewTransform.scale(scale * viewTransform.getScaleX(), scale* viewTransform.getScaleY());
		
		MultiThreadPainter.renderNow(canvas, buffer, exportViewTransform);

		ImageIO.write(buffer, "png", new java.io.File(filename));
	}
	
	private void exportPNG(String filename, Integer dpi) throws Exception { 
		double scale;
		
		try {scale =Math.ceil(((double) dpi)/java.awt.Toolkit.getDefaultToolkit().getScreenResolution());}
		catch (java.awt.HeadlessException e) {scale = Math.ceil(((double) dpi)/StencilPanel.ABSTRACT_SCREEN_RESOLUTION);}

		long width = (int) Math.round(Math.ceil(scale * canvas.getWidth()));
		long height = (int) Math.round(Math.ceil(scale * canvas.getHeight()));

		AffineTransform viewTransform = canvas.viewTransformRef(); 
		Point2D topLeft = viewTransform.transform(canvas.getBounds().getLocation(), null);
		
		if (width == 0 || height == 0) { //If nothing will display, then just show everything (usually batch mode)
			Rectangle contentBounds = canvas.contentBounds(true);
			width = (int) Math.round(Math.ceil(scale * contentBounds.width));
			height = (int) Math.round(Math.ceil(scale * contentBounds.height));
			topLeft = contentBounds.getLocation();
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
		
		exportPNG(filename, (int) width, (int) height);
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
	
	@Override
	public int paintCount() {return painter.paintCount();}
	
	@Override
	public void postRun() {painter.doUpdates();}
}
