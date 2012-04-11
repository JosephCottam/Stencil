package stencil.explorations.microbenchmarks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import stencil.interpreter.Environment;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.NumericSingleton;
import stencil.types.Converter;
import stencil.util.StencilThreadFactory;

public class DataParallelDynamicBind {	

	private static class Rule {
		private Invokeable m;
		{
			m = new ReflectiveInvokeable("add1", stencil.modules.Numerics.class);
		}
		
		public Object apply(Environment env) {
			env.ensureCapacity(env.size() +2);
			Tuple t = env.get(env.capacity()-2);
			int input = Converter.toInteger(t.get(0));
			
			for (int i=0; i<input%10; i++) {
				Object[] args = new Object[]{input};
				input = Converter.toInteger(m.invoke(args));
			}
			return input;
		}
	}
	
	private static class ParallelTask implements Callable<Object> {
		private final Rule r;
		private final int start;
		private final int length;
		private final Tuple[] concern;
		private final Object target;
		private final Object[] holding;
		
		public ParallelTask(Rule r, int start, int stop, Tuple[] concern, Object target) {
			this.r = r;
			this.start = start;
			this.length = stop-start;
			this.concern = concern;
			this.target = target;
			holding = new Object[stop-start];
		}
		
		
		@Override
		public Object call() throws Exception {
			for (int i=0; i<length; i++) {
				Tuple source = concern[start+i];
				Environment env = Environment.getDefault(Tuples.EMPTY_TUPLE, source);
				holding[i] = r.apply(env);
			}
			return null;
		}
		
		public void finish() throws Exception {
			for (int i=0; i<length; i++) {
				Array.set(target, start+i, holding[i]);
			}
		}
	}
	
	
	private final ExecutorService renderPool;
	private final List<ParallelTask> tasks; 
	private final int dataSize;
	
	public DataParallelDynamicBind(int poolSize, int dataSize) {
		renderPool = Executors.newFixedThreadPool(poolSize, new StencilThreadFactory("test", Thread.MAX_PRIORITY));
		tasks = new ArrayList(poolSize);
		this.dataSize = dataSize;

		Tuple[] concern = new Tuple[dataSize];
		Object[] target = new Object[dataSize];
		for (int i =0;i<concern.length;i++) {
			concern[i] = new NumericSingleton(i);
			target[i] = -1;
		}

		
		int stride = concern.length/poolSize; 		
		for (int i=0; i<poolSize; i++) {
			tasks.add(new ParallelTask(new Rule(), i*stride, (i+1)*stride, concern, target));
		}
	}
		
	public void test() throws Exception {
		renderPool.invokeAll(tasks);
		long start = System.currentTimeMillis();
		for (ParallelTask task: tasks) {task.finish();}
		long end = System.currentTimeMillis();
		
		System.out.printf("%1$d, %2$d, %3$d\n", tasks.size(), dataSize, end-start);
		renderPool.shutdown();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Tasks, Data, Elapse");
		
		int dataSize = 1000000;

		for (int i=1; i< 11; i++) {
			for (int reps = 0; reps < 10; reps++) {
				if (dataSize%i == 0) {
					DataParallelDynamicBind dpdb = new DataParallelDynamicBind(i, dataSize);
					dpdb.test();
				}
			}
		}
	}
	
}
