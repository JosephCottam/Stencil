package stencil.adapters.java2D.util;

import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.data.Guide2D;

public final class Painter implements Runnable, Stopable, LayerUpdateListener {
	public static final RenderingHints HIGH_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	public static final RenderingHints  LOW_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	
	private static final Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	public static RenderingHints renderQuality = HIGH_QUALITY;
	
	private boolean run = true;
	private final DisplayLayer[] layers;
	private final Canvas target;
	
	private final LayerUpdateListener.AtomicCompositeUpdate layerUpdates = new LayerUpdateListener.AtomicCompositeUpdate();
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0;
	
	//Cached transform, used to detect if a zoom/pan has occurred since the last render
	private AffineTransform priorInverseTransform= new AffineTransform();
	
	
	public Painter(DisplayLayer[] layers, Canvas target) {
		this.layers = layers;
		this.target = target;
		
		for (DisplayLayer l: layers) {
			l.addLayerUpdateListener(this);
		}
	}
	

	public void signalStop() {run = false;}
	
	public void run() {
		while (run) {
			Rectangle updateBounds = layerUpdates.clear();
			AffineTransform inverse = target.getInverseViewTransformRef();
			
			if (resized() || transformed(inverse)
				|| (updateBounds != null && updateBounds.intersects(inverse.createTransformedShape(target.getBounds()).getBounds()))) 
			{	
				BufferedImage i = selfBuffer();
				target.setBackBuffer(i);
				target.repaint();
			} 
			Thread.yield(); 
		}
	}
	
	/**Render glyphs immediately onto the passed graphics object.
	 * @param g Graphics object to render on
	 * @param clipBounds Device-space size of the rendering area
	 * */
	public void doDrawing(Graphics2D g) {
		g.addRenderingHints(renderQuality);
		AffineTransform base = g.getTransform();

		for (DisplayLayer<? extends Glyph2D> table: layers) {
			for (Glyph2D glyph: table) {
				if (!glyph.isVisible()) {continue;}
				Rectangle2D r = glyph.getBoundsReference();
				if (g.hitClip((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight())) {
					glyph.render(g, base);
				}
			}
			
			for (Guide2D guide: table.getGuides()) {guide.render(g, base);} //TODO: Guides should probably be associated with the entire panel and rendered after all layers...
		}
	}
	
	private boolean resized() {
		BufferedImage i = buffers[nextBuffer];
		if (i == null) {return false;}	//Has not been resized since last rendering, if there has been no last rendering.
		
		Rectangle target = this.target.getBounds();
		return (target.getHeight() != i.getHeight()) || (target.getWidth() != i.getWidth()); 
	}
	
	/**Has the target been transformed since it was last rendered?*/
	private final boolean transformed(AffineTransform updateCandidate) {return !priorInverseTransform.equals(updateCandidate);}
	
	private BufferedImage selfBuffer() {
		BufferedImage buffer = buffers[nextBuffer];
		Rectangle size = target.getBounds();
		AffineTransform priorTransform = target.getViewTransform();
		priorInverseTransform = target.getInverseViewTransform();
		
		if (size.width <=0 || size.height <=0) {size = DEFAULT_SIZE;}
		
		//Ensure that the buffer is the 'right' size
		if (buffer == null ||
			buffer.getWidth() != size.width ||
			buffer.getHeight() != size.height) 
		{
			buffers[nextBuffer] = newBuffer(target, size.width, size.height);
			buffer= buffers[nextBuffer];
		}
		
		Graphics2D g =null;
		try {
			g = (Graphics2D) buffer.getGraphics();	//Clear prior data off
			g.setPaint(target.getBackground());
			Rectangle bounds = new Rectangle(0,0, size.width, size.height);
			g.fill(bounds);

			g.setTransform(priorTransform);
			doDrawing(g);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (g !=null) {g.dispose();}
		}
		
		updateNextBuffer();
		return buffer;
	}

	//Taken roughly from Prefuse's Display 
    protected BufferedImage newBuffer(Canvas canvas, int width, int height) {
        BufferedImage img = null;
        if ( !GraphicsEnvironment.isHeadless() ) {
            try {
                img = (BufferedImage)canvas.createImage(width, height);
            } catch ( Exception e ) {
                img = null;
            }
        }
        if ( img == null ) {
            return new BufferedImage(width, height,
                                     BufferedImage.TYPE_INT_RGB);
        }
        return img;
    }
	
	private void updateNextBuffer() {nextBuffer = (nextBuffer+1)%(buffers.length);}
	public void layerUpdated(Rectangle update) {layerUpdates.update(update);}
}
