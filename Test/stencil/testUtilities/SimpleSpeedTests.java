package stencil.testUtilities;

import java.lang.reflect.*;

public class SimpleSpeedTests {
	public static int REPS = 10;
	
	public static long arrayTest(final int maxExp) {
		long rv=Long.MIN_VALUE;
		
		for (int exp=1; exp< maxExp; exp++) {
			long running=0;
			int size = (int) Math.pow(2,exp);
			long accesses = REPS*size;
			
			for (int r=0;r<REPS; r++) {
				int[] a = new int[size];
				long start = System.nanoTime();
				for (int i=0; i<a.length;i++) {
					Array.set(a, i,i);
				}
				long end= System.nanoTime();
				running += (end-start);
			}
			System.out.printf("%1$s, %2$s, write, reflect\n", running/accesses, size);
			
			running=0;
			for (int r=0; r<REPS; r++) {
				int[] a = new int[size];
				long start = System.nanoTime();
				for (int i=0; i< a.length; i++) {
					a[i] =i;
				}
				long end= System.nanoTime();
				running+=(end-start);
				
				for (int i=0; i< a.length; i++) {
					running +=a[i];
				}
			}
			System.out.printf("%1$s, %2$s, write, native\n", running/accesses, size);

			
			running=0;
			for (int r=0; r< REPS; r++) {
				int[] a = new int[size];
				long start = System.nanoTime();
				for (int i=0; i< a.length; i++) {
					rv += Array.getLong(a, i);
				}
				long end= System.nanoTime();
				running+=(end-start);				
			}
			System.out.printf("%1$s, %2$s, read, reflect\n", running/accesses, size);

			running=0;
			for (int r=0; r< REPS; r++) {
				int[] a = new int[size];
				long start = System.nanoTime();
				for (int i=0; i< a.length; i++) {
					rv += a[i];
				}
				long end= System.nanoTime();
				running+=(end-start);				
			}
			System.out.printf("%1$s, %2$s, read, native\n", running/accesses, size);
		}

		return rv;
	}
	
	
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
		System.out.printf("%1$d\t\tDirect invoke, no arguments\n", (end-start)/reps);	
		
		
		Method m = target.getClass().getMethod("add");
		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			m.invoke(target);
		}
		end = System.nanoTime();
		System.out.printf("%1$s\t\tReflect invoke, no arguments\n", (end-start)/reps);	


		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			target.add(1,2,3,4);
		}
		end = System.nanoTime();
		System.out.printf("%1$d\t\tDirect invoke, 4 arguments\n", (end-start)/reps);	
		
		m = target.getClass().getMethod("add", int[].class);
		start = System.nanoTime();
		int[] args = new int[]{1,2,3,4};
		for (int i=0; i< reps; i++) {
			m.invoke(target, args);
		}
		end = System.nanoTime();
		System.out.printf("%1$s\t\tReflect invoke, 4 arguments no packing\n", (end-start)/reps);	
		
		m = target.getClass().getMethod("add", int[].class);
		start = System.nanoTime();
		for (int i=0; i< reps; i++) {
			args = new int[]{1,2,3,4};
			m.invoke(target, args);
		}
		end = System.nanoTime();
		System.out.printf("%1$s\t\tReflect invoke, 4 arguments, packing\n", (end-start)/reps);	
		
	}
	
	public static void main(String[] args) throws Exception {
		int exp = 32;
		
		if (args.length>0) {exp = Integer.parseInt(args[0]);}
		
		arrayTest(exp);
		invokeTest();
	}
}
