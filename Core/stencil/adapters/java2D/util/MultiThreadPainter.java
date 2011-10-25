package stencil.adapters.java2D.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import stencil.Configure;
import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.adapters.java2D.Panel;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.Guide2D;
import stencil.interpreter.tree.Program;
import stencil.util.StencilThreadFactory;

/**Paint visualization.
 * This implementation uses a set of thread pools to perform updates and do the actual painting.
 * 
 * TODO: Explore making paint tasks render whole space always.  This simplifies simple zoom/pan updates, esp for slow-changing layers.  This makes adds a principle "full image" buffer to the main painter.  Painter tasks only need new buffers when image bounds change; Painter tasks no longer need the view transform; Response to view zoom/pan is independent of data updates. Switch to this special task if the layer hasn't change for a while. 
 * */
public final class MultiThreadPainter {	
	/**Root class for paint related tasks.
	 * Paint tasks when called execute against the layer render set;
	 * it is the responsibility of the caller to ensure that render-sets match between the different paint tasks.
	 * 
	 * */
	private static abstract class PaintTask implements Callable<BufferedImage> {
		protected BufferedImage buffer;
		protected Graphics2D g;
		protected AffineTransform base;
		protected int stateID = Integer.MAX_VALUE;

		/**Does the current painter need an update based on its backing information?
		 * This method should be as conservative about updating as safe.
		 */
		protected abstract boolean updateRequired();

		/**Ensure that the next attempt to paint will occur, regardless of any tracked state.
		 */
		protected abstract void forceUpdate();

		/**The stateID of the last rendering;*/
		public int stateID() {return stateID;}
		
		public void preRender(BufferedImage prototype, AffineTransform base) {
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
		private static final class Layer extends PaintTask {
			private final Table layer;
			private TableView view;
			
			protected volatile boolean forceUpdate = true;
			protected final Renderer renderer;
			
			public Layer(Table layer, Renderer renderer) {
				this.layer = layer;
				this.renderer = renderer;
			}
			
			public void preRender(BufferedImage prototype, AffineTransform base) {
				this.view = layer.tenured();
				super.preRender(prototype, base);
			}
			
			public boolean updateRequired() {return forceUpdate || stateID != view.stateID();}
			public void forceUpdate() {forceUpdate = true;}

			public BufferedImage call() {
				if (!updateRequired()) {return buffer;}
				renderer.render(view, g, base);
				stateID = view.stateID();
				forceUpdate = false;
				view = null;
				return buffer;
			}
			
			public String toString() {return "Layer Painter for " + layer.name();}
		}
		
		/**Paint all the guides from a given canvas.*/
		private static final class Guide extends PaintTask {
			private Guide2D guideDef;
			
			public boolean updateRequired(){return true;}
			public void forceUpdate() {}
			
			public Guide(Guide2D guide) {this.guideDef = guide;}
			public BufferedImage call() {
				
				guideDef.render(g, base);
				return buffer;
			}
			public String toString() {return "Guide Painter for " + guideDef.identifier();}
		}
		
		//Factory methods....
		public static PaintTask newTask(Table layer, Renderer renderer) {return new Layer(layer, renderer);}		
		public static PaintTask newTask(Guide2D guide) {return new Guide(guide);}
	}
	
	public static final RenderingHints HIGH_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	public static final RenderingHints LOW_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	
	public static RenderingHints renderQuality = HIGH_QUALITY;
	
	private final Table[] layers;
	private final Renderer[] renderers;
	private final List<PaintTask> painters;
	private final ExecutorService renderPool;
	
	private AffineTransform renderedViewTransform = AffineTransform.getRotateInstance(Math.PI);
	
	private final Canvas canvas;
	private final PrerenderTasks prerenderTasks;
	
	public MultiThreadPainter(Program program, Panel panel) {
		this.canvas = panel.getCanvas().getComponent();
		this.layers = canvas.layers;		

		renderers = new Renderer[layers.length];
		for (int i=0; i< layers.length; i++) {renderers[i] = LayerTypeRegistry.makeRenderer(layers[i].prototype());}
		
		renderPool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("render", Thread.MAX_PRIORITY));
		
		painters = new ArrayList();		
		prerenderTasks = new PrerenderTasks(program, panel, layers, renderers);
		


		for (int i=0; i< layers.length; i++) {
			painters.add(PaintTask.newTask(layers[i], renderers[i]));
		}
		for (Guide2D guide: canvas.getGuides()) {painters.add(PaintTask.newTask(guide));}
		
	}

	public synchronized void signalShutdown() {
		if (!renderPool.isShutdown()) {renderPool.shutdown();}
		prerenderTasks.signalShutodwn();
	}
	
	public boolean isShutdown() {return renderPool.isShutdown();}
	
	public boolean requiresUpdate(AffineTransform trans) {
		for (int i=0; i< layers.length; i++) {
			if (layers[i].stateID() != painters.get(i).stateID()) {return true;}
		}
		
		
		return prerenderTasks.requiresUpdate() || !renderedViewTransform.equals(trans);		
	}

	/**Render to the given buffer.*/
	public void render(Paint background, BufferedImage buffer, AffineTransform trans) {
		Graphics2D g = buffer.createGraphics();	
		g.addRenderingHints(LOW_QUALITY);			

		try {
			//Clear prior data off
			g.setComposite(AlphaComposite.Src);
			g.setPaint(background);
			g.fillRect(0,0, buffer.getWidth(), buffer.getHeight());
			g.setComposite(AlphaComposite.SrcOver);

			g.addRenderingHints(renderQuality);			
			try {
				List<Future<BufferedImage>> results;
				
				synchronized(canvas.tableCaptureLock) {
					for (PaintTask painter: painters) {painter.preRender(buffer, trans);}					
				}

				results = renderPool.invokeAll(painters);
				
				//Composite images as the return
				for (Future<BufferedImage> f: results) {
					g.drawImage(f.get(), 0,0, null);						
				}
			} catch (Exception e) {throw new RuntimeException("Error in multi-thread painting.", e);}

			renderedViewTransform = trans;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (g !=null) {g.dispose();}
		}
	}
	
	/**Render a list of layers to the given image.
	 * The layer at index zero is rendered first, and index length-1 is rendered last.**/
	public static void renderNow(Canvas canvas, BufferedImage image, AffineTransform viewTransform) {
		TableView[] views = new TableView[canvas.layers.length];
		synchronized(canvas.tableCaptureLock) {
			for (int i=0; i< views.length; i++) {
				views[i] = canvas.layers[i].tenured();
			}
		}
		
		Graphics2D g = image.createGraphics();	
		g.addRenderingHints(HIGH_QUALITY);

		g.setPaint(canvas.getBackground());
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setTransform(viewTransform);
		
		for (TableView view: views) {
			 Renderer renderer = LayerTypeRegistry.makeRenderer(view.schema());
			 renderer.render(view, g, viewTransform);
		}
		
		for (Guide2D def: canvas.getGuides()) {
			def.render(g, viewTransform);
		}
	}
	
	/**Run all update tasks.
	 * 
	 * To get a fully updated drawing, this should be run before drawing
	 * and no new tuples inserted between the two.
	 * */
	public void doUpdates() {
		try {prerenderTasks.prerender();}
		catch (Exception e) {throw new RuntimeException("Error running asynchronous updates.", e);}
	}

}

