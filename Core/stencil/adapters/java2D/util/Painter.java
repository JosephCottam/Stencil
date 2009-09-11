package stencil.adapters.java2D.util;

import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.data.glyphs.Point;

public final class Painter extends Thread implements Stopable {
	private Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	
	private boolean run = true;
	private final Table[] layers;
	private final GenerationTracker generations;
	private final Canvas target;
	
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0;
	
	public Painter(Table[] layers, Canvas target) {
		this.layers = layers;
		this.target = target;
		generations = new GenerationTracker(layers);
	}
	
	public void run() {
		while (run) {

			if (generations.changed() || resized()) {
				BufferedImage i = selfBuffer();
				target.setBackBuffer(i);
				target.repaint();
			}
			Thread.yield();
		}
		run=false;
	}
	
	/**Render glyphs immediately onto the passed graphics object.*/
	public void doDrawing(Graphics2D g) {
		for (Table<? extends Point> table: layers) {
			generations.fixGeneration(table);	//Prevents some types of over-painting, but not all of them
			for (Point glyph: table) {
				glyph.render(g);
			}
		}
	}
	
	private boolean resized() {
		BufferedImage i = buffers[nextBuffer];
		if (i == null) {return false;}	//Has not been resized since last rendering, if there has been no last rendering.
		Rectangle target = this.target.getBounds();
		return (target.getHeight() != i.getHeight()) || (target.getWidth() != i.getWidth()); 
	}
	
	private BufferedImage selfBuffer() {
		Graphics2D g =null;
		BufferedImage buffer = buffers[nextBuffer];
		target.getContentDimension();			//TODO: REMOVE, this is part of a hack that sets up zoon...needs to be removed AS SOON AS POSSIBLE!!!
		Rectangle size = target.getBounds();
		
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

			g.setTransform(target.getViewTransform());
			doDrawing(g);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (g !=null) {g.dispose();}
		}
		
		updateNextBuffer();
		return buffer;
	}

	public void signalStop() {run = false;}

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
	
	private void updateNextBuffer() {
		nextBuffer = (nextBuffer+1)%(buffers.length);
	}
}
