/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.display.CanvasTuple;
import stencil.display.DisplayCanvas;
import stencil.display.DisplayGuide;
import stencil.display.DisplayLayer;
import stencil.interpreter.tree.*;


/**Some of this is derived from Prefuse's display and related objects.*/

public final class Canvas extends DisplayCanvas {	
	private BufferedImage buffer;
	
	private AffineTransform viewTransform = new AffineTransform();
	private AffineTransform inverseViewTransform = new AffineTransform(); //Default transform is its own inverse
	private final Map<Selector, Guide2D> guides  = new ConcurrentHashMap();
	
	final DoubleBufferLayer<? extends Glyph2D>[] layers;

	/**Point used in many navigation operations.*/
	private final Point2D tempPoint = new Point2D.Double();
	
	public Canvas(stencil.interpreter.tree.Canvas def, Layer[] layers) {
		String colorKey = (String) def.specializer().get(CanvasTuple.CanvasAttribute.BACKGROUND_COLOR.name());
		Color c = stencil.types.color.ColorCache.get(colorKey);
		this.setBackground(c);
		
		//Copy display layers out of the layer objects
		this.layers = new DoubleBufferLayer[layers.length];
		for (int i=0;i< layers.length;i++) {
			this.layers[i] = (DoubleBufferLayer) layers[i].getDisplayLayer();
		}
		setDoubleBuffered(false);	//TODO: Use the BufferStrategy instead of manually double buffering
		setOpaque(true);
	}
	
	public synchronized void paintComponent(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}
	
	public synchronized void setBackBuffer(BufferedImage i) {
		this.buffer = i;
		this.repaint();
	}
	
	public DisplayGuide getGuide(Selector sel) {return guides.get(sel);}
	public void addGuide(Selector sel, Guide2D guide) {guides.put(sel, guide);}
	public boolean hasGuide(Selector sel) {return guides.containsKey(sel);}
	public Collection<Guide2D> getGuides() {return guides.values();}
	
	/**What are the bounds of everything currently on this canvas 
	 * (not just visible or in window).
	 * 
	 * @return
	 */
	public Rectangle getContentBounds(boolean includeGuides) {
		if (layers.length == 0) {return new Rectangle(0,0,0,0);}
		final Rectangle2D bounds = new Rectangle(layers[0].getView().getBoundsReference());
		for (DisplayLayer<? extends Glyph2D> l: layers) {
			bounds.add(l.getView().getBoundsReference());
		}
		
		if (includeGuides) {
			for (Guide2D g: guides.values()) {
				bounds.add(g.getBoundsReference());
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
        catch (NoninvertibleTransformException e ) {throw new Error("Supposedly impossible error occured.", e);}
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
		
		Rectangle2D viewBounds = getInverseViewTransform().createTransformedShape(getBounds()).getBounds2D();

		target.setLocation(viewBounds.getCenterX(), viewBounds.getCenterY());
		return target; 
	}
	

    
    public synchronized void setViewTransform(AffineTransform transform) throws NoninvertibleTransformException {
    	this.viewTransform = transform;
    	try {this.inverseViewTransform = transform.createInverse();}
    	catch (NoninvertibleTransformException e) {throw e;}
    }
    
    
    /**Use this transform to convert values from the absolute system
     * to the screen system.
     */
	public synchronized AffineTransform getViewTransform() {return new AffineTransform(viewTransform);}
	public synchronized AffineTransform getViewTransformRef() {return viewTransform;}
	
	/**Use this transform to convert screen values to the absolute/canvas
	 * values.
	 */
	public synchronized AffineTransform getInverseViewTransform() {return new AffineTransform(inverseViewTransform);}
	public synchronized AffineTransform getInverseViewTransformRef() {return inverseViewTransform;}
}
