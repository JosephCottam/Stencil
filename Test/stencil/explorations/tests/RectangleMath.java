package stencil.explorations.tests;

import java.awt.*;
import java.awt.geom.*;

public class RectangleMath {
	private static void report(Rectangle2D r) {
		System.out.println(r);
		System.out.printf("\t empty: %1$s\n", r.isEmpty());
		System.out.printf("\t bounds: %1$s\n", r.getBounds());
		System.out.println();
	}
	
	
	public static void main(String[] args) throws Exception {
		Rectangle2D empty = new Rectangle(0,0,0,0);
		report(empty);

		Rectangle2D root = new Rectangle(0,0,-1,-1);
		report(root);

		Rectangle2D r1 = new Rectangle(0,0,100,100);
		System.out.println("Union with: " + r1);
		report(root.createUnion(r1));
		
		Rectangle2D r2 = new Rectangle(-100,-100,1,1);
		System.out.println("Union with: " + r2);
		report(root.createUnion(r2));
		
		Rectangle2D r3 = new Rectangle(100,100,1,1);
		System.out.println("Union with: " + r3);
		report(root.createUnion(r3));
		
	}
}
