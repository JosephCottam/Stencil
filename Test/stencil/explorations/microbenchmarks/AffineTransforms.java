package stencil.explorations.microbenchmarks;

import java.awt.geom.*;

public class AffineTransforms {
	private static final int ITERS = 100000;
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		for (int i=0; i< ITERS; i++) {
			AffineTransform trans = new AffineTransform();
			trans.rotate(ITERS);
		}
		long end = System.currentTimeMillis();
		System.out.printf("New every time: %1$s avg\n", (end-start)/((double)ITERS));
		
		

		AffineTransform trans = new AffineTransform();
		long start2 = System.currentTimeMillis();
		for (int i=0; i< ITERS; i++) {
			trans.setToIdentity();
			trans.rotate(ITERS);
		}
		long end2 = System.currentTimeMillis();
		System.out.printf("Reset every time: %1$s avg\n", (end2-start2)/((double)ITERS));

	}
}
