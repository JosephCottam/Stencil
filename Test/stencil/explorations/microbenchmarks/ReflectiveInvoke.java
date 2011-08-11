package stencil.explorations.microbenchmarks;

import java.lang.reflect.*;

public class ReflectiveInvoke {

	@SuppressWarnings("unused")
	private static class TestObject {
		public int sum=0;
		public void add() {sum++;}
		public void add(int... is) {
			for (int i: is) {i += i;}
		}
	}

	public static void invokeTest() throws Exception {
		TestObject target = new TestObject();
		int reps = 10000000;
		
		long start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			target.add();
		}
		long end = System.nanoTime();
		System.out.printf("%1$d ns\t\tDirect invoke, no arguments\n", (end-start)/reps);	
		
		
		Method m = target.getClass().getMethod("add");
		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			m.invoke(target);
		}
		end = System.nanoTime();
		System.out.printf("%1$d ns\t\tReflect invoke, no arguments\n", (end-start)/reps);	


		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			target.add(1,2,3,4);
		}
		end = System.nanoTime();
		System.out.printf("%1$d ns\t\tDirect invoke, 4 arguments\n", (end-start)/reps);	
		
		m = target.getClass().getMethod("add", int[].class);
		start = System.nanoTime();
		int[] args = new int[]{1,2,3,4};
		for (int i=0; i< reps; i++) {
			m.invoke(target, args);
		}
		end = System.nanoTime();
		System.out.printf("%1$d ns\t\tReflect invoke, 4 arguments no packing\n", (end-start)/reps);	
		
		m = target.getClass().getMethod("add", int[].class);
		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			args = new int[]{1,2,3,4};
			m.invoke(target, args);
		}
		end = System.nanoTime();
		System.out.printf("%1$d ns\t\tReflect invoke, 4 arguments, packing\n", (end-start)/reps);	
		
	}
	
	public static void main(String[] args) throws Exception {
		invokeTest();
	}
}
