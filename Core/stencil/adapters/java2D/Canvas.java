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
import java.util.List;

import javax.swing.JComponent;

import stencil.adapters.java2D.data.glyphs.Point;
import stencil.adapters.java2D.data.Table;
import stencil.adapters.java2D.util.GenerationTracker;
import stencil.adapters.java2D.util.Painter;
import stencil.display.CanvasTuple;
import stencil.parser.tree.Layer;


/**Some of this is derived from Prefuse's display and related objects.*/

public final class Canvas extends JComponent {	
	final Painter painter;	
	BufferedImage buffer;
	
	private AffineTransform viewTransform = new AffineTransform();
	private AffineTransform inverseViewTransform = new AffineTransform(); //Default transform is its own inverse
	
	private final GenerationTracker tablesTracker;
	
	private Rectangle2D contentBounds;
	final Table<? extends Point>[] layers;

	/**Point used in many navigation operations.*/
	private final Point2D tempPoint = new Point2D.Double();

	
	public Canvas(List<Layer> layers) {
		this.setBackground((Color) CanvasTuple.CanvasAttribute.BACKGROUND_COLOR.getDefaultValue());
		
		this.layers = new Table[layers.size()];
		for (int i=0;i< layers.size();i++) {
			this.layers[i] = (Table) layers.get(i).getDisplayLayer();
		}
		this.painter = new Painter(this.layers, this);
		painter.start();

		setDoubleBuffered(false);	//TODO: Use the BufferStrategy instead of manually double buffering
		setOpaque(true);
		tablesTracker = new GenerationTracker(this.layers);		
	}
	
	public void dispose() {painter.signalStop();}
		
	public void paintComponent(Graphics g) {g.drawImage(buffer, 0, 0, null);}
	
	public void setBackBuffer(BufferedImage i) {this.buffer = i;}
	
	public Rectangle getContentBounds() {
		Rectangle2D bounds =contentBounds;
		if (contentBounds == null || tablesTracker.changed()) {
			for (Table<? extends Point> t: layers) {
				tablesTracker.fixGeneration(t);
				for (Point p: t) {
					if (bounds == null) {bounds = p.getBounds();}
					else {bounds.add(p.getBounds());}
				}
			}
		} 
		return bounds.getBounds();
	}
	
	/**Zoom anchored on the given screen point to the given scale.*/
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
        try {
            inverseViewTransform = viewTransform.createInverse();
        } catch ( Exception e ) {throw new Error("Supposedly impossible error occured.", e);}
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
        try {
        	inverseViewTransform = viewTransform.createInverse();
        } catch ( Exception e ) {throw new Error("Supposedly impossible error occured.", e);}
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
        try {
        	inverseViewTransform = viewTransform.createInverse();
        } catch ( Exception e ) {throw new Error("Supposedly impossible error occured.", e);}
	}

	
    /**Get the current scale factor factor (in cases
     * where it is significant, this is the X-scale).
     */
    public double getScale() {return viewTransform.getScaleX();}
    
    public void setViewTransform(AffineTransform transform) throws NoninvertibleTransformException {
    	this.viewTransform = transform;
    	this.inverseViewTransform = transform.createInverse();
    }
    
    
    /**Use this transform to convert values from the absolute system
     * to the screen system.
     */
	public AffineTransform getViewTransform() {return new AffineTransform(viewTransform);}
	public AffineTransform getViewTransformRef() {return viewTransform;}
	
	/**Use this transform to convert screen values to the absolute/canvas
	 * values.
	 */
	public AffineTransform getInverseViewTransform() {return new AffineTransform(inverseViewTransform);}
	public AffineTransform getInverseViewTransformRef() {return inverseViewTransform;}

}
