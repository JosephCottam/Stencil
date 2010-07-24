package stencil.adapters.java2D.util;

import java.awt.AlphaComposite;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import stencil.Configure;
import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.display.DisplayLayer;
import stencil.display.LayerView;
import stencil.parser.string.MakeViewPoint;
import stencil.parser.tree.DynamicRule;
import stencil.parser.tree.Program;
import stencil.parser.tree.util.Path;
import stencil.tuple.Tuple;
import stencil.util.StencilThreadFactory;

/**Paint a panel.
 * 
 * TODO: Make paint tasks render whole space always.  This simplifies simple zoom/pan updates, esp for slow-moving layers.  This makes adds a principle "full image" buffer to the main painter.  Painter tasks only need new buffers when image bounds change; Painter tasks no longer need the view transform; Response to view zoom/pan is independent of data updates. 
 * */
public final class Painter implements Runnable {
	//Prevents updates from occurring while actively painting (not required if store wont' throw a ConcurrentModificationException)
	//TODO: Move store to a concurrent collection and remove this lock
	private final Object paintLock = new Object(); 
	
	/**Root class for paint related tasks.*/
	private static abstract class PaintTask implements Callable<BufferedImage> {
		protected BufferedImage buffer;
		protected Graphics2D g;
		protected AffineTransform base;

		/**Does the current painter need an update based on its backing information?
		 * This method should be as conservative about updating as safe.
		 */
		protected abstract boolean updateRequired();

		/**Ensure that the next attempt to paint will occur, regardless of any tracked state.
		 */
		protected abstract void forceUpdate();

		
		public void createBuffer(BufferedImage prototype, AffineTransform base) {
			//Only allocate a new buffer if the shape changed, otherwise use the old buffer
			if (buffer == null || prototype.getWidth() != buffer.getWidth() || prototype.getHeight() != buffer.getHeight()) {
				buffer = new BufferedImage(prototype.getWidth(), prototype.getHeight(), 
						BufferedImage.TYPE_INT_ARGB);
				g = (Graphics2D) buffer.getGraphics();
				g.addRenderingHints(renderQuality);
				forceUpdate();
			}
			
			if (this.base == null || !this.base.equals(base)) {forceUpdate();}
		
			if (updateRequired()) {				
				g.setTransform(AffineTransform.getTranslateInstance(0, 0));
				g.setComposite(AlphaComposite.Clear);	//Clear off old data
				g.fillRect(0,0, buffer.getWidth(), buffer.getHeight());
				g.setComposite(AlphaComposite.SrcOver);
				g.setTransform(base);
				this.base = base;
			}
		}		
		
		/**Paint a single layer to a buffer.*/
		private static final class LayerPainter extends PaintTask {
			private final LayerView<Glyph2D> view;
			protected int stateID = Integer.MAX_VALUE;
			protected boolean forceUpdate = true;
			
			LayerPainter(LayerView view) {this.view = view;}
					
			public boolean updateRequired() {return forceUpdate || stateID != view.getStateID();}
			public void forceUpdate() {forceUpdate = true;}
			
			public BufferedImage call() {
				if (!updateRequired()) {return buffer;}

				for (Glyph2D glyph: view.renderOrder()) {
					if (!glyph.isVisible()) {continue;}
					Rectangle2D r = glyph.getBoundsReference();

					if (g.hitClip((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight())) {
						glyph.render(g, base);
					}
				}
				
				stateID = view.getStateID();
				forceUpdate = false;
				
				return buffer;
			}
			
			public String toString() {return "Layer Painter for " + view.getLayerName();}
		}
		
		/**Paint all the guides from a given canvas.*/
		private static final class GuidePainter extends PaintTask {
			private Guide2D guideDef;
			
			public boolean updateRequired(){return true;}
			public void forceUpdate() {}
			
			public GuidePainter(Guide2D guide) {this.guideDef = guide;}
			public BufferedImage call() {
				guideDef.render(g, base);
				return buffer;
			}
			public String toString() {return "Guide Painter for " + guideDef.getAttribute();}
		}
		
		//Factory methods....
		public static PaintTask newTask(LayerView view) {return new LayerPainter(view);}		
		public static PaintTask newTask(Guide2D guide) {return new GuidePainter(guide);}
	}
	
	public static final RenderingHints HIGH_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	public static final RenderingHints LOW_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	
	private static final Rectangle DEFAULT_SIZE =new Rectangle(0,0,1,1);
	public static RenderingHints renderQuality = HIGH_QUALITY;
	
	private final DisplayLayer[] layers;
	private final int[] stateIDs;
	private       Collection<PaintTask> painters;
	private final ExecutorService renderPool;
	private final ExecutorService updatePool;
	private final Canvas target;
	private final Panel panel;
	
	private final BufferedImage[] buffers = new BufferedImage[2];
	private int nextBuffer =0; 
	protected boolean keepRunning = true;
	
	private final Map<Object, UpdateTask> updaters = new HashMap();
	private AffineTransform renderedViewTransform = AffineTransform.getRotateInstance(Math.PI);

	public Painter(final DoubleBufferLayer[] layers, final Canvas target, final Panel panel) {
		this.layers = layers;
		this.target = target;
		this.panel = panel;
		this.stateIDs = new int[layers.length];
		
		Arrays.fill(stateIDs, Integer.MAX_VALUE);
		
		renderPool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("render"));
		updatePool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("update"));
		painters = new ArrayList();
	}

	public void dispose() {
		if (!renderPool.isShutdown()) {renderPool.shutdown();}
		if (!updatePool.isShutdown()) {updatePool.shutdown();}
	}
	
	public synchronized void signalStop() {
		keepRunning =false;
		renderPool.shutdownNow();
		updatePool.shutdownNow();
	}
	
	public void run() {
		while(keepRunning) {
			if (requiresUpdate()) {runOnce();}
			Thread.yield();
		}
	}
	
	private synchronized void runOnce() {
		if (renderPool.isShutdown()) {return;}
		
		BufferedImage i = selfBuffer();
		target.setBackBuffer(i);
		target.repaint();
	}

	
	public final boolean requiresUpdate() { 
		BufferedImage img = buffers[nextBuffer];
		if (img == null) {return true;}
		
		for (int i=0; i< stateIDs.length; i++) {
			if (layers[i].getStateID() != stateIDs[i]) {return true;}
		}
		
		Rectangle targetBounds = this.target.getBounds();
		return (targetBounds.getHeight() != img.getHeight()) 
				|| (targetBounds.getWidth() != img.getWidth())
				|| !renderedViewTransform.equals(target.getViewTransformRef());
		
	}

	/**Render glyphs immediately onto the passed graphics object.
	 * @param g Graphics object to render on
	 * @param clipBounds Device-space size of the rendering area
	 * */
	public void doDrawing(BufferedImage buffer, Graphics2D g) {
		g.addRenderingHints(renderQuality);
		
		synchronized (paintLock) {
			renderedViewTransform = g.getTransform();
			g.setTransform(AffineTransform.getTranslateInstance(0,0));
			try {
				for (PaintTask painter: painters) {
					painter.createBuffer(buffer, renderedViewTransform);
				}

				List<Future<BufferedImage>> results =  renderPool.invokeAll(painters);
				//Composite images as the return
				for (Future<BufferedImage> f: results) {
					g.drawImage(f.get(), 0,0, null);						
				}
				
				//record that painting occured
				for(int i=0; i<layers.length;i++) {
					stateIDs[i] = layers[i].getStateID();
				}
			} catch (Exception e) {throw new RuntimeException("Error in mulit-thread painting.", e);}
		}
	}
	
		
	private BufferedImage selfBuffer() {
		doUpdates();
		
		BufferedImage buffer = buffers[nextBuffer];
		Rectangle size = target.getBounds();
		AffineTransform priorTransform = target.getViewTransform();
		
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
			g = buffer.createGraphics();	//Clear prior data off
			g.setComposite(AlphaComposite.Src);
			g.setPaint(target.getBackground());
			g.fillRect(0,0, size.width, size.height);
			g.setComposite(AlphaComposite.SrcOver);
			
			g.setTransform(priorTransform);
			doDrawing(buffer, g);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (g !=null) {g.dispose();}
		}
		
		updateNextBuffer();
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
	
	public void addDynamic(Glyph2D glyph, DynamicRule rule, Tuple source) {
		Path path = new Path(rule);
		DynamicUpdateTask updateTask;
		if (updaters.containsKey(rule)) {
			updateTask = (DynamicUpdateTask) updaters.get(rule);
		} else {
			DisplayLayer layer= null;
			String ruleLayerName=rule.getGroup().getContext().getName();
			for (DisplayLayer t: layers) {if (t.getName().equals(ruleLayerName)) {layer = t; break;}}
			assert layer != null : "Table null after name-based search.";
			updateTask = new DynamicUpdateTask(layer, rule);
			updaters.put(rule, updateTask);
		}
		updateTask.addUpdate(source, glyph);
	}
	
	
	public void addUpdaterTask(UpdateTask task) {updaters.put(task, task);}
	
	/**Run all update tasks.
	 * 
	 * To get a fully updated drawing, this should be run before drawing
	 * and no new tuples inserted between the two.
	 * */
	public void doUpdates() {
		try {
			synchronized(panel.visLock) {
				synchronized(paintLock) {
					for (DisplayLayer layer: layers) {
						((DoubleBufferLayer) layer).changeGenerations();
					}
					
					MakeViewPoint.viewPoint(panel.getProgram());
					
					List<Future<Finisher>> results = updatePool.invokeAll(updaters.values());  //PROBLEM: Assumes dynamic updates do not depend on the state of the layer.  Does that make sense???  Otherwise, sequence of updates will matter.
															  									  //SOLUTION: Introduce rounds of dynamic binding.  Syntax is ":n*.  Round is automatically determined EXCEPT when a circularity exists.  Then round must be explicit.
					for (Future<Finisher> f: results) {
						Finisher finalizer = f.get();
						finalizer.finish();
					}
					
					painters = new ArrayList(); 
					
					for (int i=0; i< layers.length; i++) {
						DoubleBufferLayer layer = (DoubleBufferLayer) layers[i]; 
						LayerView view = layer.changeGenerations();
						painters.add(PaintTask.newTask(view));
					}
					for (Guide2D guide: target.getGuides()) {painters.add(PaintTask.newTask(guide));}		
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error running asynchronous updates.", e);
		}
	}
}

