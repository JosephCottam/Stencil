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
package stencil.adapters.java2D.util;

import java.awt.Cursor;
import java.awt.event.*;
import java.awt.geom.*;

import stencil.adapters.java2D.Canvas;


public class ZoomPanHandler implements MouseListener, MouseMotionListener{
    public static final double MIN_SCALE = Double.MIN_VALUE; //Minimum single-step change
    public static final double MAX_SCALE = Double.MAX_VALUE;   //Maximum single-step change
	
    private static final int ZOOM_BUTTON = InputEvent.BUTTON2_MASK;
    private static final int PAN_BUTTON = InputEvent.BUTTON1_MASK;
	
    private Point2D down = new Point2D.Float();
    private int yLast;
    
	/**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) { 	
        if (buttonEquals(e, ZOOM_BUTTON) ) {
            Canvas canvas = (Canvas)e.getComponent();

            canvas.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            
            canvas.getInverseViewTransformRef().transform(e.getPoint(), down);
            yLast = e.getY();
        } else if (buttonEquals(e, PAN_BUTTON)) {
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            down = e.getPoint();
        }
    }
    
    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if (buttonEquals(e, ZOOM_BUTTON) ) {
        	Canvas canvas = (Canvas)e.getComponent();
            
            int y = e.getY();
            int dy = y-yLast;
            double zoom = 1 + ((double)dy) / 100;

            int cursor = Cursor.N_RESIZE_CURSOR;
            canvas.setCursor(Cursor.getPredefinedCursor(cursor));
            
            zoom(canvas, down, zoom, true);
            
            yLast = y;
        } else if (buttonEquals(e, PAN_BUTTON)) {
        	Canvas canvas = (Canvas)e.getComponent();
            double x = e.getX(),   y = e.getY();
            double dx = x-down.getX(), dy = y-down.getY();

            canvas.pan(dx,dy);
            down = e.getPoint();
        }
    }

    /**
     * Zoom the given display at the given point by the zoom factor,
     * in either absolute (item-space) or screen co-ordinates.
     * @param display the Display to zoom
     * @param p the point to center the zoom upon
     * @param zoom the scale factor by which to zoom
     * @param abs if true, the point p should be assumed to be in absolute
     * coordinates, otherwise it will be treated as screen (pixel) coordinates
     */
    protected void zoom(Canvas canvas, Point2D p, double zoom, boolean abs) {
        double scale = canvas.getScale();
        double result = scale * zoom;

        if ( result < MIN_SCALE ) {
            zoom = MIN_SCALE/scale;
        } else if ( result > MAX_SCALE ) {
            zoom = MAX_SCALE/scale;
        }       
        
        if ( abs ) {canvas.zoomAbs(p,zoom);}
        else {canvas.zoom(p,zoom);}
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        if (buttonEquals(e, ZOOM_BUTTON) || buttonEquals(e, PAN_BUTTON)) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        } 
    }
    
    private static final boolean buttonEquals(MouseEvent e, int button) {
    	return (e.getModifiers() & button) == button;
    }

	public void mouseClicked(MouseEvent e) { 
		if (e.getClickCount() == 2) {
			Canvas canvas = (Canvas)e.getComponent();
			Rectangle2D content = canvas.getContentBounds();
			Rectangle2D space = canvas.getBounds();

			double w = space.getWidth()/content.getWidth();
			double h = space.getHeight()/content.getHeight();
			double scale = Math.min(w, h);
			scale = scale/canvas.getScale();
			Point2D center = new Point2D.Double(content.getCenterX(), content.getCenterY());  
			
			canvas.zoomAbs(center, scale);
			canvas.panToAbs(center);
		}
		
	}

	public void mouseEntered(MouseEvent e) {/*Ignored.*/}

	public void mouseExited(MouseEvent e) {/*Ignored.*/}

	public void mouseMoved(MouseEvent e) {/*Ignored.*/}
}
