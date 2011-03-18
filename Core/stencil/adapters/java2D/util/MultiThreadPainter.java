package stencil.adapters.java2D.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.display.DisplayLayer;
import stencil.display.LayerView;
import stencil.interpreter.tree.DynamicRule;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Program;
import stencil.util.StencilThreadFactory;

/**Paint visualization.
 * This implementation uses a set of thread pools to perform updates and do the actual painting.
 * 
 * TODO: Make a paint task to render whole space always.  This simplifies simple zoom/pan updates, esp for slow-moving layers.  This makes adds a principle "full image" buffer to the main painter.  Painter tasks only need new buffers when image bounds change; Painter tasks no longer need the view transform; Response to view zoom/pan is independent of data updates. Switch to this special task if the layer hasn't change for a while. 
 * */
public final class MultiThreadPainter {	
	/**Root class for paint related tasks.*/
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
		private static final class Layer extends PaintTask {
			private final DisplayLayer layer;
			protected boolean forceUpdate = true;

			public Layer(DisplayLayer layer) {this.layer = layer;}
			
			public boolean updateRequired() {return forceUpdate || stateID != layer.viewpoint().getStateID();}
			public void forceUpdate() {forceUpdate = true;}
			
			public BufferedImage call() {
				if (!updateRequired()) {return buffer;}
				LayerView<Glyph2D> view = layer.viewpoint();

				
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
			
			public String toString() {return "Layer Painter for " + layer.getName();}
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
			public String toString() {return "Guide Painter for " + guideDef.getAttribute();}
		}
		
		//Factory methods....
		public static PaintTask newTask(DisplayLayer layer) {return new Layer(layer);}		
		public static PaintTask newTask(Guide2D guide) {return new Guide(guide);}
	}
	
	public static final RenderingHints HIGH_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	public static final RenderingHints LOW_QUALITY = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	
	public static RenderingHints renderQuality = HIGH_QUALITY;
	
	private final DoubleBufferLayer[] layers;
	private final List<PaintTask> painters;
	private final ExecutorService renderPool;
	private final ExecutorService updatePool;
	
	private final List<UpdateTask> guideUpdaters = new ArrayList();
	private final Map<DynamicRule, DynamicUpdateTask> dynamicUpdaters = new HashMap();

	private AffineTransform renderedViewTransform = AffineTransform.getRotateInstance(Math.PI);
	
	private final Canvas canvas;

	
	/**@param Canvas Canvas being rendered on (TODO: currently only used to acquire guides, remove when guides are translated into layers)
	 * @param layers Layers to render
	 * @param program The program used for the visualization
	 */
	public MultiThreadPainter(Canvas canvas, DoubleBufferLayer[] layers, Program program) {
		this.layers = layers;
		this.canvas = canvas;
		
		renderPool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("render"));
		updatePool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("update"));
		
		painters = new ArrayList();		
		
		
		for (Guide g: program.canvas().guides()) {guideUpdaters.add(new GuideTask(g, canvas));}			
		for (DynamicRule rule : program.allDynamics()) {
			DoubleBufferLayer layer= null;
			for (DoubleBufferLayer t: layers) {if (t.getName().equals(rule.layerName())) {layer = t; break;}}
			assert layer != null : "Table null after name-based search.";
			
			DynamicUpdateTask updateTask = new DynamicUpdateTask(layer, rule);
			dynamicUpdaters.put(rule, updateTask);
		}
		

		for (int i=0; i< layers.length; i++) {
			DoubleBufferLayer layer = (DoubleBufferLayer) layers[i]; 
			painters.add(PaintTask.newTask(layer));
		}
		for (Guide2D guide: canvas.getGuides()) {painters.add(PaintTask.newTask(guide));}
		
	}

	public synchronized void signalShutdown() {
		if (!renderPool.isShutdown()) {renderPool.shutdown();}
		if (!updatePool.isShutdown()) {updatePool.shutdown();}
	}
	
	public boolean isShutdown() {return renderPool.isShutdown();}
	
	public boolean requiresUpdate(AffineTransform trans) {
		for (int i=0; i< layers.length; i++) {
			if (layers[i].getStateID() != painters.get(i).stateID()) {return true;}
		}
		for (UpdateTask task: guideUpdaters) {if (task.needsUpdate()) {return true;}}
		for (UpdateTask task: dynamicUpdaters.values()) {if (task.needsUpdate()) {return true;}}
		
		return !renderedViewTransform.equals(trans);		
	}

	/**Render to the given buffer.*/
	public void render(Paint background, BufferedImage buffer, AffineTransform trans) {
		Graphics2D g = buffer.createGraphics();	

		try {
			//Clear prior data off
			g.setComposite(AlphaComposite.Src);
			g.setPaint(background);
			g.fillRect(0,0, buffer.getWidth(), buffer.getHeight());
			g.setComposite(AlphaComposite.SrcOver);

			g.addRenderingHints(renderQuality);			
			try {
				List<Future<BufferedImage>> results;
				
				for (PaintTask painter: painters) {painter.createBuffer(buffer, trans);}
				synchronized(canvas.renderLock) {results = renderPool.invokeAll(painters);}
				
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
	
	public void addTask(GuideTask task) {guideUpdaters.add(task);}
	
	/**Run all update tasks.
	 * 
	 * To get a fully updated drawing, this should be run before drawing
	 * and no new tuples inserted between the two.
	 * */
	public void doUpdates() {
		try {
			synchronized(canvas.renderLock) {					//Prevent competing renders
				synchronized(canvas.visLock) { 					//Suspend analysis until the viewpoint is ready
					for (DisplayLayer layer: layers) {
						((DoubleBufferLayer) layer).changeGenerations();
					}
	
					for (UpdateTask ut: dynamicUpdaters.values()) {ut.viewpoint();}
					for (UpdateTask ut: guideUpdaters) {ut.viewpoint();}
				}												//Resume analysis while rendering completes
				
				executeAll(dynamicUpdaters.values());
				executeAll(guideUpdaters);
				
			}
		} catch (Exception e) {
			throw new RuntimeException("Error running asynchronous updates.", e);
		}
	}
	
	/**Replacement method for a thread-pool invokeAll when using an update task.
	 * Executes the task, and then executes the finishers sequentially.
	 * 
	 * @param targets
	 * @throws Exception
	 */
	private void executeAll(Collection targets) throws Exception {
		List<Future<Finisher>> results = updatePool.invokeAll(targets);
		
		//TODO: Finisher could PROBABLY be removed if the layer were a column store...
		//       This would mean that any dynamic update calculates an entire column in the store.  
		//       If the column order is correct, and there is only one consumes block then the new column can just be swapped in.
		//       Multiple consumes blocks require column blending...which can probably be done quickly but still requires work and might require a finsiher...		
		for (Future<Finisher> f: results) {
			Finisher finalizer = f.get();
			finalizer.finish();
		}
	}
}

