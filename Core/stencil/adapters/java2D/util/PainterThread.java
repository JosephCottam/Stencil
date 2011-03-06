package stencil.adapters.java2D.util;

import java.awt.Color;
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
	
	protected volatile boolean keepRunning = true;
	
	public PainterThread(final DoubleBufferLayer[] layers, final Canvas target, final Panel panel) {
		this.target = target;
		this.painter = new MultiThreadPainter(target, layers, panel.getProgram());
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
	}

	
	private BufferedImage selfBuffer() {
		Rectangle size;
		Color background;
		AffineTransform viewTransform;

		painter.doUpdates();

		//Gather info in a thread-safe manner
		synchronized(target.visLock) {
			size = target.getBounds();
			background = target.getBackground();
			viewTransform = target.getViewTransform();			
		}

		
		if (size.width <=0 || size.height <=0) {size = DEFAULT_SIZE;}
			
		BufferedImage buffer = newBuffer(target, size.width, size.height);
	
		try {
			painter.render(background, buffer, viewTransform);
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
		
	public void doUpdates() {painter.doUpdates();}
}

