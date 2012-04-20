package stencil.adapters.java2D.util;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;


/**Automatically paints a panel.
 **/
public final class PainterThread implements Runnable {
	/**Amount of time to sleep after a checking if rendering is required. 
	 * Setting this value to zero results in just a yield call.
	 * Setting this value to -1 results in no delay.
	 */
	public static int DELAY = 0;
	
	/**Static monitoring variable.  Not reliable when more than one painter thread is instantiated.**/
	public static int paintCount =0;

	private static final Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	
	private final MultiThreadPainter painter; 

	private final Canvas target;
	
	private int used =0;
	private int unused = 1;
	
	//The buffer currently being used by the canvas is always in position indicated by member variable 'used'
	private final BufferedImage[] backBuffers = new BufferedImage[2];
	
	protected volatile boolean keepRunning = true;
	
	public PainterThread(final Panel panel) {
		this.target = panel.getCanvas().getComponent();
		this.painter = new MultiThreadPainter(panel.getProgram(), panel);
		paintCount = 0;
	}

	@Override
	public synchronized void finalize() {painter.signalShutdown();}
	
	public synchronized void signalStop() {
		keepRunning =false;
		painter.signalShutdown();
	}
	
	@Override
	public void run() {
		while(keepRunning) {
			if (requiresUpdate()) {runOnce();}
			else {
				try {Thread.sleep(33);} //HACK: Keeps from busy waiting...but a condition variable is better
				catch (InterruptedException e) {throw new RuntimeException("Error sleeping on low paint activity.", e);}				
			}
			
			if (DELAY == 0) {Thread.yield();}
			else if (DELAY >0) {
				try {Thread.sleep(DELAY);}
				catch (InterruptedException e) {throw new RuntimeException("Error delaying between renders.", e);}
			}
		}
	}
	
	private boolean requiresUpdate() {
		painter.doUpdates();

		AffineTransform trans = target.viewTransformRef();
		boolean requires = painter.requiresUpdate(trans);
		return requires;
	}
	
	private synchronized void runOnce() {
		if (painter.isShutdown()) {return;}
		BufferedImage i = selfBuffer();
		target.setBackBuffer(i);
		swapBuffers();
		paintCount++;
	}

	
	
	private BufferedImage selfBuffer() {
		Rectangle size;
		Color background;
		AffineTransform viewTransform;

		//synchronized(target.visLock) { 			//Enable and add matching parenthesis for render-state lock


		size = target.getBounds();
		background = target.getBackground();
		viewTransform = target.viewTransform();			
		
		if (size.width <=0 || size.height <=0) {size = DEFAULT_SIZE;}
			
		BufferedImage buffer = nextBuffer(size);
	
		try {
			painter.render(background, buffer, viewTransform);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private BufferedImage nextBuffer(Rectangle size) {
		if (backBuffers[unused] == null 
				|| size.width != backBuffers[unused].getWidth() 
				|| size.height != backBuffers[unused].getHeight()) {backBuffers[unused] = newBuffer(target, size.width, size.height);}
		return backBuffers[unused];
	}
	
	private void swapBuffers() {
		int hold = unused;
		unused = used;
		used = hold;
	}
	
	/**Create a new back buffers
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

    /**Force updates to run now.  Used when rendering will be pushed to something other
     * than the internal buffers (e.g., non-self buffering).
     */
	public void doUpdates() {painter.doUpdates();}
	
	public int paintCount() {return paintCount;}
}

