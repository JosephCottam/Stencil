package stencil.explorations.microbenchmarks;

import java.lang.reflect.Array;

public class ReflectiveArray {
	private static final int REPS = 10;
	
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


	public static void main(String[] args) throws Exception {
		int exp = 32;
		
		if (args.length>0) {exp = Integer.parseInt(args[0]);}
		System.out.println("Avg Access, size, r/w, config");
		arrayTest(exp);
	}
	
}
