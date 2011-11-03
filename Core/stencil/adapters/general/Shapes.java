package stencil.adapters.general;


import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import stencil.adapters.general.Registrations.Registration;

/**These shape codes were copied and modified from Prefuse's ShapeRenderer.
 **/

//final because it just a collection of utilities and should never be instantiated (so you can't override it and get an instance)
public final class Shapes {
	/**Which items may be used with the 'SHAPE' attribute*/
	public static enum StandardShape {CROSS, DIAMOND, ELLIPSE, HEXAGON, STAR, RECTANGLE, TRIANGLE_UP, TRIANGLE_DOWN, TRIANGLE_LEFT, TRIANGLE_RIGHT, NONE}


 	public static final Registration DEFAULT_REGISTRATION = Registration.CENTER;
	private static final StandardShape DEFAULT_SHAPE = StandardShape.RECTANGLE;

	private Shapes() {/*Utility class. Not instantiable.*/}

	public static Shape getShape(StandardShape shapeType, double x, double y, double width, double height) {		
		Shape shape = null;

		double[] niced = nice(x,y,width,height);
		x = niced[0];
		y = niced[1];
		width = niced[2];
		height = niced[3];
		
		Double minDimension = Math.min(width, height);

		if (shapeType == null) {shapeType = DEFAULT_SHAPE;}


		switch (shapeType) {
			case RECTANGLE: shape = Shapes.rectangle(x,y, width, height); break;
			case CROSS: shape = Shapes.cross(x,y, minDimension); break;
			case ELLIPSE: shape = Shapes.ellipse(x,y, width, height); break;
			case DIAMOND: shape = Shapes.diamond(x,y, minDimension); break;
			case HEXAGON: shape = Shapes.hexagon(x,y, minDimension); break;
			case STAR: shape = Shapes.star(x,y, minDimension); break;
			case TRIANGLE_UP: shape =Shapes.triangle_up(x,y, minDimension); break;
			case TRIANGLE_DOWN:shape =Shapes.triangle_down(x,y, minDimension); break;
			case TRIANGLE_LEFT: shape =Shapes.triangle_left(x,y, minDimension); break;
			case TRIANGLE_RIGHT: shape =Shapes.triangle_right(x,y, minDimension); break;
			case NONE: break; //for none, do nothing!
			default: throw new AssertionError("ShapePNode does not have renderer for all standard shapes (encountered shape " + shapeType + ").");
		}

		return shape;
	}
	
	/**Ensure that all width/heights are positive; modify x and y appropriately.**/
	private static final double[] nice(double x, double y, double width, double height) {
		if (width <0) {
			x = x + width;
			width = -width;
		}
		
		if (height<0) {
			y = y - height;
			height = -height;
		}
		
		return new double[]{x,y,width,height};
	}


    /**
     * Returns a rectangle of the given dimensions.
     */
	static Shape rectangle(double x, double y, double width, double height) {
    	Rectangle2D m_rect = new Rectangle2D.Double(x, y,width,height);
        return m_rect;
    }

    /**
     * Returns an ellipse of the given dimensions.
     */
	static Shape ellipse(double x, double y, double width, double height) {
    	Ellipse2D m_ellipse = new Ellipse2D.Double();
        m_ellipse.setFrame(x, y, width, height);
        return m_ellipse;
    }

    /**
     * Returns a up-pointing triangle of the given dimensions.
     */
	static Shape triangle_up(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        m_path.reset();
        m_path.moveTo(x,y+height);
        m_path.lineTo(x+height/2, y);
        m_path.lineTo(x+height, (y+height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a down-pointing triangle of the given dimensions.
     */
	static Shape triangle_down(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        m_path.reset();
        m_path.moveTo(x,y);
        m_path.lineTo(x+height, y);
        m_path.lineTo(x+height/2, (y+height));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a left-pointing triangle of the given dimensions.
     */
	static Shape triangle_left(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

    	m_path.reset();
        m_path.moveTo(x+height, y);
        m_path.lineTo(x+height, y+height);
        m_path.lineTo(x, y+height/2);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a right-pointing triangle of the given dimensions.
     */
	static Shape triangle_right(double xx, double yy, double hh)  {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        m_path.reset();
        m_path.moveTo(x,y+height);
        m_path.lineTo(x+height, y+height/2);
        m_path.lineTo(x, y);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a cross shape of the given dimensions.
     * Resulting cross only has float precision, but doubles
     * are taken for consitancy with the rest of the framework.
     */
	static Shape cross(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        float h14 = 3*height/8, h34 = 5*height/8;
        m_path.reset();
        m_path.moveTo(x+h14, y);
        m_path.lineTo(x+h34, y);
        m_path.lineTo(x+h34, y+h14);
        m_path.lineTo(x+height, y+h14);
        m_path.lineTo(x+height, y+h34);
        m_path.lineTo(x+h34, y+h34);
        m_path.lineTo(x+h34, y+height);
        m_path.lineTo(x+h14, y+height);
        m_path.lineTo(x+h14, y+h34);
        m_path.lineTo(x, y+h34);
        m_path.lineTo(x, y+h14);
        m_path.lineTo(x+h14, y+h14);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a star shape of the given dimensions.
     */
	static Shape star(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        float s = (float)(height/(2*Math.sin(Math.toRadians(54))));
        float shortSide = (float)(height/(2*Math.tan(Math.toRadians(54))));
        float mediumSide = (float)(s*Math.sin(Math.toRadians(18)));
        float longSide = (float)(s*Math.cos(Math.toRadians(18)));
        float innerLongSide = (float)(s/(2*Math.cos(Math.toRadians(36))));
        float innerShortSide = innerLongSide*(float)Math.sin(Math.toRadians(36));
        float innerMediumSide = innerLongSide*(float)Math.cos(Math.toRadians(36));

        m_path.reset();
        m_path.moveTo(x, y+shortSide);
        m_path.lineTo((x+innerLongSide),(y+shortSide));
        m_path.lineTo((x+height/2),y);
        m_path.lineTo((x+height-innerLongSide),(y+shortSide));
        m_path.lineTo((x+height),(y+shortSide));
        m_path.lineTo((x+height-innerMediumSide),(y+shortSide+innerShortSide));
        m_path.lineTo((x+height-mediumSide),(y+height));
        m_path.lineTo((x+height/2),(y+shortSide+longSide-innerShortSide));
        m_path.lineTo((x+mediumSide),(y+height));
        m_path.lineTo((x+innerMediumSide),(y+shortSide+innerShortSide));
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a hexagon shape of the given dimensions.
     */
	 static Shape hexagon(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        float width = height/2;

        m_path.reset();
        m_path.moveTo(x, y+0.5f*height);
        m_path.lineTo(x+0.5f*width, y);
        m_path.lineTo(x+1.5f*width, y);
        m_path.lineTo(x+2.0f*width, y+0.5f*height);
        m_path.lineTo(x+1.5f*width, y+height);
        m_path.lineTo(x+0.5f*width, y+height);
        m_path.closePath();
        return m_path;
    }

    /**
     * Returns a diamond shape of the given dimensions.
     */
	static Shape diamond(double xx, double yy, double hh) {
    	GeneralPath m_path = new GeneralPath();
    	float x = (float) xx;
    	float y = (float) yy;
    	float height = (float) hh;

        m_path.reset();
        m_path.moveTo(x,(y+0.5f*height));
        m_path.lineTo((x+0.5f*height),y);
        m_path.lineTo((x+height),(y+0.5f*height));
        m_path.lineTo((x+0.5f*height),(y+height));
        m_path.closePath();
        return m_path;
    }
}
