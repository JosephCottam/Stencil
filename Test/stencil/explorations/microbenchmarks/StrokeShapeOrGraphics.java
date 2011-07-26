package stencil.explorations.microbenchmarks;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class StrokeShapeOrGraphics {
	public static final int ITERATIONS = 100;
	public static void main(String[] args) {
		BufferedImage img = new BufferedImage(100,100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1f, new float[]{.5f,.2f,.5f,.1f},.1f);

		long startShape = System.currentTimeMillis();
		for (int i=0; i< ITERATIONS; i ++) {
			Shape s = new Rectangle2D.Double(i,i, 100,100);
			s = stroke.createStrokedShape(s);
			g.draw(s);
		}
		long endShape = System.currentTimeMillis();
		System.out.println("Stroked shape: " + (endShape-startShape));
		

		long start = System.currentTimeMillis();
		for (int i=0; i< ITERATIONS; i ++) {
			Stroke original = g.getStroke();
			g.setStroke(stroke);
			Shape s = new Rectangle2D.Double(i,i, 100,100);
			g.draw(s);
			g.setStroke(original);
		}
		long end = System.currentTimeMillis();
		System.out.println("Stroked graphics: " + (end-start));

		
	}

}
