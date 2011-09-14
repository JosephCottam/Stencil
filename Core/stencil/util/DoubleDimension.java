package stencil.util;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public final class DoubleDimension extends Dimension2D {
	public double width;
	public double height;

	public DoubleDimension(Rectangle2D r) {
		width = r.getWidth();
		height = r.getHeight();
	}
	
	public DoubleDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public DoubleDimension(Double width, Double height) {
		this.width = width;
		this.height = height;
	}

	public double getHeight() {return height;}
	public double getWidth() {return width;}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	/**What is the overall length of this dimension?**/
	public double getLength() {return Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));}
}
