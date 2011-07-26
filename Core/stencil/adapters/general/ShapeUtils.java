package stencil.adapters.general;

import java.awt.geom.Rectangle2D;

public final class ShapeUtils {
	private ShapeUtils() {}
	
	/**Adds two rectangles, modifying the first one. 
	 * If either is empty, the first argument will be set to the non-empty one.*/
	public static void add(Rectangle2D target, Rectangle2D more) {
		double x = more.getX();
		double y = more.getY();
		double w = more.getWidth();
		double h = more.getHeight();
		
		x = Double.isNaN(x) ? 0 : x;
		y = Double.isNaN(y) ? 0 : y;
		w = Double.isNaN(w) ? 0 : w;
		h = Double.isNaN(h) ? 0 : h;

		if (target.isEmpty()) {
			target.setFrame(x,y,w,h);
		} else if (!more.isEmpty()) {
			target.add(new Rectangle2D.Double(x,y,w,h));
		}
	}


	/**Same as add, but will not modify either arg; returns a new rectangle*/
	public static Rectangle2D union(Rectangle2D r1, Rectangle2D r2) {
		Rectangle2D r = new Rectangle2D.Double(0,0,-1,-1);
		add(r, r1);
		add(r, r2);
		return r;
	}

}
