package stencil.explorations.microbenchmarks;

public class Autobox {
	private static final int ITERS  = 1000000;
	public static class Source {
		int i=0;
		public Integer nextAuto() {return i++;}
		public Integer nextManual() {return new Integer(i++);}
		public int nextBoxless() {return i++;}
	}
	
	public static void main(String[] args) {
		Source s = new Source();
		long sum =0;
		long start = System.currentTimeMillis();
		for (int i=0; i< ITERS; i++) {
			sum += s.nextAuto();
		}
		long end = System.currentTimeMillis();
		long elapse = end-start;
		System.out.printf("Autoboxing avg (sum) : %1$s (%2$s)\n", elapse/((double) ITERS), sum);
		
		
		s = new Source();
		sum =0;
		start = System.currentTimeMillis();
		for (int i=0; i< ITERS; i++) {
			sum += s.nextManual();
		}
		end = System.currentTimeMillis();
		elapse = end-start;
		System.out.printf("Manual boxing avg (sum) : %1$s (%2$s)\n", elapse/((double) ITERS), sum);

		

		s = new Source();
		sum =0;
		start = System.currentTimeMillis();
		for (int i=0; i< ITERS; i++) {
			sum += s.nextBoxless();
		}
		end = System.currentTimeMillis();
		elapse = end-start;
		System.out.printf("No boxing avg (sum) : %1$s (%2$s)\n", elapse/((double) ITERS), sum);

		
	}
}
