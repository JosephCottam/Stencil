package stencil.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**Based on Executors.defaultThreadFactory*/
public final class StencilThreadFactory implements ThreadFactory {
    static final AtomicInteger threadNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final int priority;
	
	public StencilThreadFactory(String prefix, int priority) {
		group = new ThreadGroup(prefix); 
		this.priority = priority;
	}

	public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, group.getName() + "-" + threadNumber.getAndIncrement() + " (pool)");
		t.setDaemon(false);
		t.setPriority(priority);
		return t;
	}

}
