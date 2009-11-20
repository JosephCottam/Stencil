package stencil.adapters.java2D.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.interpreter.DynamicRule;
import stencil.parser.tree.Rule;
import stencil.tuple.Tuple;

public class DynamicUpdater implements Runnable, Stopable {
	private boolean run = true;
	private final Map<Rule, DynamicUpdateTask> tasks = new ConcurrentHashMap();

	public void addDynamicUpdate(Glyph2D glyph, Rule rule, Tuple source, DisplayLayer layer) {
		DynamicUpdateTask updater;
		
		if (!tasks.containsKey(rule)) {
			Rule dynamicRule = DynamicRule.toDynamic(rule);
			updater = new DynamicUpdateTask(layer, dynamicRule);
			tasks.put(rule, updater);
		}

		updater = tasks.get(rule);
		updater.addUpdate(source, glyph);
	}
	
	
	public void run() {
		while (run) {
			runOnce();
			Thread.yield();
		}		
	}
	
	public void runOnce() {for (DynamicUpdateTask task: tasks.values()) {task.runOnce();}}

	public void signalStop() {run = false;}

}
