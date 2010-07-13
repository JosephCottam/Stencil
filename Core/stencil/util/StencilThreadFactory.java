package stencil.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**Based on Executors.defaultThreadFactory*/
public final class StencilThreadFactory implements ThreadFactory {
	private final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);

	public StencilThreadFactory(String prefix) {
		group = new ThreadGroup(prefix); 
	}

	public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, group.getName() + "-" + threadNumber.getAndIncrement() + " (pool)");
		if (t.isDaemon())
		t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
		t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

}
