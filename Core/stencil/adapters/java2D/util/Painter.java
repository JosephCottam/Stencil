package stencil.adapters.java2D.util;

import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.data.glyphs.Point;

public final class Painter extends Thread {
	private boolean run = true;
	private final Table[] layers;
	private final Canvas target;
	
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0;
	
	public Painter(Table[] layers, Canvas target) {
		this.layers = layers;
		this.target = target;
	}
	
	public void run() {
		while (run) {
			Image i = selfBuffer();
			target.setBackBuffer(i);
			Thread.yield();
		}
	}
	
	private void doDrawing(Graphics2D g) {
		for (Table<? extends Point> table: layers) {
			for (Point glyph: table) {
				glyph.render(g);
			}
		}
	}
			
	private Image selfBuffer() {
		Graphics2D g =null;
		BufferedImage buffer = buffers[nextBuffer];
		Rectangle size = target.getContentDimension();
		
		//Ensure that the buffer is the 'right' size
		if (buffer == null ||
			buffer.getHeight() != size.height
			|| buffer.getWidth() != size.width) 
		{
			buffers[nextBuffer] = newBuffer(size.height, size.width);
			buffer= buffers[nextBuffer];
		}
		
		try {
			g = (Graphics2D) buffer.getGraphics();
//			doDrawing(g);
		} finally {
			if (g !=null) {g.dispose();}
		}
		
		updateNextBuffer();
		return buffer;
	}

	public void signalStop() {run = false;}

	//Taken from Prefuse's Display 
    protected BufferedImage newBuffer(int width, int height) {
        BufferedImage img = null;
        if ( !GraphicsEnvironment.isHeadless() ) {
            try {
                img = (BufferedImage)target.createImage(width, height);
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
	
	private int updateNextBuffer() {
		return (nextBuffer+1)%(buffers.length);
	}
}
