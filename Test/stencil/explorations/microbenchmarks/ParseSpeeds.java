package stencil.explorations.microbenchmarks;

import java.util.regex.*;

public class ParseSpeeds {
	private static final String TEST = "one thing, and another";
	private static final long ITERATIONS = (long) Math.floor(Math.pow(10, 6));
	
	private static final void fixed(long iterations) {
		int end1 = TEST.indexOf(",");
		int start2 = end1+1;
		for (long i=0; i< iterations; i++) {
			String[] parts = new String[2];
			parts[0] = TEST.substring(0, end1);
			parts[1] = TEST.substring(start2);
		}
	}
	
	private static final void re(long iterations) {
		String pattern = "\\s*,\\s*";
		Pattern p = java.util.regex.Pattern.compile(pattern);
		
		for (long i =0; i<iterations; i++) {
			String[] parts = p.split(TEST);
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		long startRE = System.currentTimeMillis();
		re(ITERATIONS);
		long endRE = System.currentTimeMillis();
		long totalRE = endRE-startRE;
		
		long startFixed = System.currentTimeMillis();
		fixed(ITERATIONS);
		long endFixed = System.currentTimeMillis();
		long totalFixed = endFixed-startFixed;
		
		System.out.println("Re: " + totalRE);
		System.out.println("Fixed: " + totalFixed);
		System.out.println("Proportion: " + 100* (totalFixed/(double) totalRE));
	}
}
