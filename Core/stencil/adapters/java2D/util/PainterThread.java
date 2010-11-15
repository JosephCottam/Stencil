package stencil.adapters.java2D.util;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;
import stencil.adapters.java2D.data.DoubleBufferLayer;


/**Automatically paints a panel.
 **/
public final class PainterThread implements Runnable {
	private static final Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	
	private final MultiThreadPainter painter; 

	private final Canvas target;
	
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0; 
	protected volatile boolean keepRunning = true;
	
	public PainterThread(final DoubleBufferLayer[] layers, final Canvas target, final Panel panel) {
		this.target = target;
		this.painter = new MultiThreadPainter(target, layers, panel.visLock, panel.getProgram());
	}

	public synchronized void finalize() {painter.signalShutdown();}
	
	public synchronized void signalStop() {
		keepRunning =false;
		painter.signalShutdown();
	}
	
	public void run() {
		while(keepRunning) {
			if (requiresUpdate()) {runOnce();}
			Thread.yield();
		}
	}
	
	private boolean requiresUpdate() {
		Rectangle bounds = target.getBounds();
		AffineTransform trans = target.getViewTransformRef();
		boolean requires = painter.requiresUpdate(trans, bounds);
		return requires;
	}
	
	private synchronized void runOnce() {
		if (painter.isShutdown()) {return;}
		
		BufferedImage i = selfBuffer();
		target.setBackBuffer(i);
		updateNextBuffer();
	}

	
	private BufferedImage selfBuffer() {
		painter.doUpdates();
		
		BufferedImage buffer = buffers[nextBuffer];
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
			painter.render(target.getBackground(), buffer, target.getViewTransform());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return buffer;
	}

	/**Create a new buffer if required.
	 * The buffer will be created for the target canvas.
	 * A buffer will only be 'required' if the size or transparency of the passed
	 * buffer do not match the passed target size or passed transparency.
	 * 
	 * @param target
	 * @param buffer
	 * @param transparent
	 * @return
	 */
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
	
	public void doUpdates() {painter.doUpdates();}
}

