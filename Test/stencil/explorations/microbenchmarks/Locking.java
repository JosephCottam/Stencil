package stencil.explorations.microbenchmarks;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Locking {
	public static enum LockType {SYNCH, LOCK, SEMA, NONE}

	public static class Task implements Runnable {
		public volatile boolean run = true;
		public final long life;
		public long start;
		public int count =0;
		
		private final LockType type;
		public Task(LockType type, long life) {
			this.type = type;
			this.life = life;
		}

		
		@Override
		public void run() {
			start = System.currentTimeMillis();
			
			while (alive()) {
				switch (type) {
				case SYNCH: synch(); break;
				case LOCK: lock(); break;
				case SEMA: sema(); break;
				case NONE: none(); break;
				}
				count++;
			}
		}
		private boolean alive() {return life > (System.currentTimeMillis()-start);}
	}
	
	public static final Object SYNCH_LOCK = new Object();
	public static void synch() {synchronized(SYNCH_LOCK) {work();}}
	
	public static final ReentrantLock LOCK = new ReentrantLock();
	public static void lock() {
		LOCK.lock();
		work();
		LOCK.unlock();
	}
	
	public static final Semaphore SEMA = new Semaphore(1);
	public static void sema() {
		SEMA.acquireUninterruptibly();
		work();
		SEMA.release();
	}
	
	public static void none() {work();}

	public static String work() {
		String result = new String();
		for (int i=0; i<1000;i++) {
			result = result + Math.log(i);
		}
		return result;
	}
	
	
	public static void runOne(LockType type, long life) throws InterruptedException {
		Task task1 = new Task(type, life);
		Task task2 = new Task(type, life);
		Thread t1 = new Thread(task1);
		Thread t2 = new Thread(task2);
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		
		System.out.printf("%1$s : %2$s + %3$s = %4$s\n", type, task1.count, task2.count, task1.count + task2.count);
	}
	
	public static void main(String[] args) throws InterruptedException {
		long life = args.length > 0 ? Long.parseLong(args[0]) : 10000;
		
		
		for (int i=0; i< 10; i++) {
			runOne(LockType.NONE, life);
			runOne(LockType.SYNCH, life);
			runOne(LockType.SEMA, life);
			runOne(LockType.LOCK, life);
			System.out.println();
		}
	}
}
