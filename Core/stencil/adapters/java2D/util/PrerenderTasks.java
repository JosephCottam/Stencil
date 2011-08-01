package stencil.adapters.java2D.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import stencil.Configure;
import stencil.adapters.java2D.Panel;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.Renderer;
import stencil.display.Display;
import stencil.interpreter.tree.DynamicRule;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Program;
import stencil.parser.ParserConstants;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.util.StencilThreadFactory;


/**Coordinates the pre-render tasks for a program.
 * This includes dynamic bind and guide calculations, along with any book-keeping for generation-based persistence.
 * 
 * @author jcottam
 *
 */
public class PrerenderTasks {
	/**Tuple presented to trigger events on the Render stream.**/
	private static final SourcedTuple RENDER_TUPLE = new SourcedTuple.Wrapper(ParserConstants.RENDER_STREAM, ArrayTuple.from()); 
	
	private final ExecutorService updatePool = Executors.newFixedThreadPool(Configure.threadPoolSize, new StencilThreadFactory("update", Thread.MAX_PRIORITY));
	private final Map<DynamicRule, DynamicUpdateTask> dynamicUpdaters = new HashMap();
	private final List<UpdateTask> guideUpdaters = new ArrayList();
	private final List<SimpleUpdateTask> simpleUpdaters = new ArrayList();

	private final Panel panel;
	private final Table[] layers;
	private final Renderer[] renderers;
	
	public PrerenderTasks(Program program, Panel panel, Table[] layers, Renderer[] renderers) {
		this.panel = panel;
		
		this.layers = layers;
		this.renderers = renderers;
		
		for (DynamicRule rule : program.allDynamics()) {
			Table layer= null;
			for (Table t: layers) {if (t.name().equals(rule.layerName())) {layer = t; break;}}
			assert layer != null : "Table null after name-based search.";
			
			DynamicUpdateTask updateTask = new DynamicUpdateTask(layer, rule);
			dynamicUpdaters.put(rule, updateTask);
		}
		
		for (Guide g: program.allGuides()) {guideUpdaters.add(new GuideTask(g, panel.getCanvas().getComponent()));}
		for (Table l: layers) {simpleUpdaters.add(new SimpleUpdateTask(l));}
	}
	
	public void signalShutodwn() {if (!updatePool.isShutdown()) {updatePool.shutdown();}}
	
	public synchronized void prerender() throws Exception {
		synchronized(panel.getCanvas().getComponent().visLock) { 					//Suspend analysis until the viewpoint is ready
			for (Table layer: layers) {
				layer.changeGenerations();
			}

			for (UpdateTask ut: dynamicUpdaters.values()) {ut.viewpoint();}
			for (UpdateTask ut: guideUpdaters) {ut.viewpoint();}
		}										//Resume analysis while rendering completes
		
		//TODO: Can here down be spun off into a separate thread?  
			//If so, return a future from this method.  Regular renderer would need to redo rendering when the future indicates everything is done.
			//Might keep the FPS up higher by faster response to render requests
		
		executeAll(simpleUpdaters);
		executeAll(dynamicUpdaters.values());
		for (Table layer: layers) {layer.viewpoint().dynamicComplete();}		//TODO: One parallel task per per-table?   (Must happen after because of 'find' in dynamic bindings)
		for (int i=0; i<layers.length; i++) {
			Table layer = layers[i];
			if (layer.viewpoint().unchanged()) {continue;}	//TODO: skip if there were no changes, BUT don't skip if there are viewTransform changes...  

			//Renderer-based field updates
			Renderer renderer = renderers[i];
			renderer.calcFields(layer.viewpoint(), panel.getCanvas().getComponent().viewTransform());
		}
		synchronized(panel.getCanvas().getComponent().tableCaptureLock) {
			for (Table layer: layers) {layer.merge(layer.viewpoint());}		//TODO: One (parallel) task per per-table? 
		}

		executeAll(guideUpdaters);
		
		if (Display.canvas != null && Display.view != null) {
			panel.processTuple(RENDER_TUPLE);
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
		for (Future<Finisher> f: results) {
			Finisher finalizer = f.get();
			finalizer.finish();
		}
	}
	
	public boolean requiresUpdate() {
		for (UpdateTask task: guideUpdaters) {if (task.needsUpdate()) {return true;}}
		for (UpdateTask task: dynamicUpdaters.values()) {if (task.needsUpdate()) {return true;}}
		return false;
	}
}
