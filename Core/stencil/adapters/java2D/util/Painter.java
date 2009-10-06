package stencil.adapters.java2D.util;

import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.data.Guide2D;

public final class Painter implements Runnable, Stopable {
	private static final Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	private static final RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
	
	private boolean run = true;
	private final DisplayLayer[] layers;
	private final GenerationTracker generations;
	private final Canvas target;
	
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0;
	AffineTransform priorTransform=new AffineTransform();
	
	
	public Painter(DisplayLayer[] layers, Canvas target) {
		this.layers = layers;
		this.target = target;
		generations = new GenerationTracker(layers);
	}
	

	public void signalStop() {run = false;}
	
	public void run() {
		while (run) {
			if (generations.changed() || resized() || transformed()) {
				BufferedImage i = selfBuffer();
				target.setBackBuffer(i);
				target.repaint();
			}
			Thread.yield();
		}
		run=false;
	}
	
	/**Render glyphs immediately onto the passed graphics object.*/
	public void doDrawing(Graphics2D g, AffineTransform base) {
		g.addRenderingHints(rh);
		for (DisplayLayer<? extends Glyph2D> table: layers) {
			generations.fixGeneration(table);	//Prevents some types of unnecessary re-rendering, but not all of them
			for (Glyph2D glyph: table) {
				Rectangle r = glyph.getBoundsReference().getBounds();
				if (glyph.isVisible() 
					&& g.hitClip(r.x, r.y, r.width, r.height)) {
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
	private boolean transformed() {return !priorTransform.equals(target.getViewTransformRef());}
	
	private BufferedImage selfBuffer() {
		Graphics2D g =null;
		BufferedImage buffer = buffers[nextBuffer];
		Rectangle size = target.getBounds();
		priorTransform = target.getViewTransform();
		
		if (size.width <=0 || size.height <=0) {size = DEFAULT_SIZE;}
		
		//Ensure that the buffer is the 'right' size
		if (buffer == null ||
			buffer.getWidth() != size.width ||
			buffer.getHeight() != size.height) 
		{
			buffers[nextBuffer] = newBuffer(target, size.width, size.height);
			buffer= buffers[nextBuffer];
		}
		
		try {
			g = (Graphics2D) buffer.getGraphics();	//Clear prior data off
			g.setPaint(target.getBackground());
			g.fillRect(0, 0, size.width, size.height);

			g.setTransform(priorTransform);
			doDrawing(g, priorTransform);
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
}
