package stencil.adapters.java2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import stencil.adapters.general.ShapeUtils;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.render.guides.Guide2D;
import stencil.display.CanvasTuple;
import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.interpreter.tree.*;


/**Some of this is derived from Prefuse's display and related objects.*/

public final class Canvas extends DisplayCanvas {
	private volatile BufferedImage buffer;
	
	private AffineTransform viewTransform = new AffineTransform();
	private AffineTransform inverseViewTransform = new AffineTransform(); //Default transform is its own inverse
	private final Map<String, Guide2D> guides  = new ConcurrentHashMap();
	
	public final Table[] layers;

	/**Point used in many navigation operations.*/
	private final Point2D tempPoint = new Point2D.Double();
	
	public Canvas(Specializer canvasSpec, Layer[] layers) {
		String colorKey = (String) canvasSpec.get(CanvasTuple.BACKGROUND_COLOR);
		Color c = stencil.types.color.ColorCache.get(colorKey);
		this.setBackground(c);
		
		//Copy display layers out of the layer objects
		this.layers = new Table[layers.length];
		for (int i=0;i< layers.length;i++) {
			this.layers[i] = (Table) layers[i].implementation();
		}
		setDoubleBuffered(false);	//TODO: Use the BufferStrategy instead of manually double buffering
		setOpaque(true);
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}
	
	public void setBackBuffer(BufferedImage i) {
		this.buffer = i;
		this.repaint();
	}
	
	public DisplayGuide getGuide(String identifier) {return guides.get(identifier);}
	public void addGuide(Guide2D guide) {guides.put(guide.identifier(), guide);}
	public boolean hasGuide(String identifier) {return guides.containsKey(identifier);}
	public Collection<Guide2D> getGuides() {return guides.values();}
	
	/**What are the bounds of everything currently on this canvas 
	 * (not just visible or in window).
	 * 
	 * @return
	 */
	public Rectangle contentBounds(boolean includeGuides) {
		if (layers.length == 0) {return new Rectangle(0,0,0,0);}
		final Rectangle2D bounds = new Rectangle(0,0,-1,-1);
		for (Table l: layers) {
			ShapeUtils.add(bounds, l.tenured().getBoundsReference());
		}
		
		if (includeGuides) {
			for (Guide2D g: guides.values()) {
				ShapeUtils.add(bounds, g.getBoundsReference());
			}
		}
		
		if (bounds.isEmpty()) {return new Rectangle(0,0,0,0);}
		else {return bounds.getBounds();}
	}
	
	/**Zooms anchored on the given screen point TO the given scale.*/
	public synchronized void zoomTo(final Point2D p, double scale) {
		inverseViewTransform.transform(p, tempPoint);
		zoomToAbs(tempPoint, scale);
	}

	/**Zooms anchored on the given screen point TO the given scale.*/
	public synchronized void zoomToAbs(final Point2D p, double scale) {
		zoomToAbs(p, scale, scale);
	}
	
	/**Zooms anchored on the given screen point TO the given scale.*/
	public synchronized void zoomToAbs(final Point2D p, double scaleX, double scaleY) {
		zoomAbs(p, scaleX/viewTransform.getScaleX(), scaleY/viewTransform.getScaleY());
	}



	
	/**Zoom anchored on the given screen point by the given scale.*/
	public synchronized void zoom(final Point2D p, double scale) {
		inverseViewTransform.transform(p, tempPoint);
		zoomAbs(tempPoint, scale);
	}
	
	/**Zoom anchored on the given absolute point (e.g. canvas 
	 * under the identity transform) to the given scale.
	 */
	public synchronized void zoomAbs(final Point2D p, double scale) {
		zoomAbs(p, scale, scale);
	}
	
	public synchronized void zoomAbs(final Point2D p, double scaleX, double scaleY) {
		double zx = p.getX(), zy = p.getY();
        viewTransform.translate(zx, zy);
        viewTransform.scale(scaleX,scaleY);
        viewTransform.translate(-zx, -zy);
        try {setViewTransform(viewTransform);}
        catch (NoninvertibleTransformException e ) {
        	try {setViewTransform(new AffineTransform());}
			catch (NoninvertibleTransformException e1) {}	//Default transform is invertible...so everything is safe
        	throw new Error("Supposedly impossible error occured.", e);
        }
	}
	
    /**
     * Pans the view provided by this display in screen coordinates.
     * @param dx the amount to pan along the x-dimension, in pixel units
     * @param dy the amount to pan along the y-dimension, in pixel units
     */
    public synchronized void pan(double dx, double dy) {
    	tempPoint.setLocation(dx, dy);
    	inverseViewTransform.transform(tempPoint, tempPoint);
        double panx = tempPoint.getX();
        double pany = tempPoint.getY();
        tempPoint.setLocation(0, 0);
        inverseViewTransform.transform(tempPoint, tempPoint);
        panx -= tempPoint.getX();
        pany -= tempPoint.getY();
        panAbs(panx, pany);
    }
    
    /**
     * Pans the view provided by this display in absolute (i.e. item-space)
     * coordinates.
     * @param dx the amount to pan along the x-dimension, in absolute co-ords
     * @param dy the amount to pan along the y-dimension, in absolute co-ords
     */
    public synchronized void panAbs(double dx, double dy) {
    	viewTransform.translate(dx, dy);
        try {setViewTransform(viewTransform);}
        catch (NoninvertibleTransformException e ) {throw new Error("Supposedly impossible error occured.", e);}
    }
	
	/**Pan so the display is centered on the given screen point.*/
	public synchronized void panTo(final Point2D p) {
        inverseViewTransform.transform(p, tempPoint);
        panToAbs(tempPoint);
	}
	
	/**Pan so the display is centered on the given canvas
	 * point.
	 */
	public synchronized void panToAbs(final Point2D p) {
        double sx = viewTransform.getScaleX();
        double sy = viewTransform.getScaleY();
        double x = p.getX(); x = (Double.isNaN(x) ? 0 : x);
        double y = p.getY(); y = (Double.isNaN(y) ? 0 : y);
        x = getWidth() /(2*sx) - x;
        y = getHeight()/(2*sy) - y;
        
        double dx = x-(viewTransform.getTranslateX()/sx);
        double dy = y-(viewTransform.getTranslateY()/sy);

        viewTransform.translate(dx, dy);
        try {setViewTransform(viewTransform);}
        catch (NoninvertibleTransformException e ) {throw new Error("Supposedly impossible error occured.", e);}
	}

	
    /**Get the current scale factor factor (in cases
     * where it is significant, this is the X-scale).
     */
    public synchronized double getScale() {return viewTransform.getScaleX();}
    
	/**What is the current center of the screen (in canvas coordinates).
	 * 
	 *  @param target Store in this point2D.  If null a new point2D will be created.
	 **/
	public Point2D getPanAbs(Point2D target) {
		if (target == null) {target = new Point2D.Double();}
		
		Rectangle2D viewBounds = inverseViewTransform().createTransformedShape(getBounds()).getBounds2D();

		target.setLocation(viewBounds.getCenterX(), viewBounds.getCenterY());
		return target; 
	}
	

    
    public synchronized void setViewTransform(AffineTransform transform) throws NoninvertibleTransformException {
    	this.viewTransform = transform;
    	this.inverseViewTransform = transform.createInverse();
    }
    
    
    /**Use this transform to convert values from the absolute system
     * to the screen system.
     */
	public synchronized AffineTransform viewTransform() {return new AffineTransform(viewTransform);}
	public synchronized AffineTransform viewTransformRef() {return viewTransform;}
	
	/**Use this transform to convert screen values to the absolute/canvas
	 * values.
	 */
	public synchronized AffineTransform inverseViewTransform() {return new AffineTransform(inverseViewTransform);}
	public synchronized AffineTransform inverseViewTransformRef() {return inverseViewTransform;}
}
