package stencil.explorations.microbenchmarks;

import java.util.Arrays;

public class ArrayCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("size, creationTotal, copyTotal, creationPer, copyPer");
		for (int i=1; i<100; i++) {
			int[] source = new int[i*100000];
			Arrays.fill(source, i);
			
			long start1 = System.currentTimeMillis();
			int[] target = new int[source.length];
			long end1 = System.currentTimeMillis();
			long createElapse = end1-start1;
			double createAvg = createElapse/((double) i);

			
			long start2 = System.currentTimeMillis();
			System.arraycopy(source, 0, target, 0, target.length);
			long end2 = System.currentTimeMillis();
			long copyElapse = end2-start2;
			double copyAvg = copyElapse/((double) i);
			
			System.out.printf("%1$,d, %2$s, %3$s, %4$f, %4$f\n", source.length, createElapse, copyElapse, createAvg, copyAvg);
		}
		
	}

}
