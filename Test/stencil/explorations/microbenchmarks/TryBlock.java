package stencil.explorations.microbenchmarks;

/**Investigate the overhead of entering a try block.*/
public class TryBlock {
	private static final int iterations = 100000;
	
	private static final void doSomething(int i) {
		System.out.print(".");
	}
	
	public static void noBlock() {
		for (int i=0;i<iterations; i++) {doSomething(i);}
	}

	public static void block() {
		for (int i=0;i<iterations; i++) {
			try {doSomething(i);}
			catch (Exception e) {throw new RuntimeException(e);}
		}
	}
	
	public static void main(String[] args) {
		long start = System.nanoTime();
		noBlock();
		long end = System.nanoTime();
		
		System.out.println("\nNo block:\t " + (end-start));
		
		start = System.nanoTime();
		block();
		end = System.nanoTime();
		
		System.out.println("\nBlock:\t " + (end-start));
		
	}

}
