/*
 * Copyright (c) 2002-@year@, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in tree and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of tree code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of the University of Maryland nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Piccolo was written at the Human-Computer Interaction Laboratory www.cs.umd.edu/hcil by Jesse Grosjean
 * under the supervision of Ben Bederson. The Piccolo website is www.cs.umd.edu/hcil/piccolo.
 */
package stencil.adapters.piccoloDynamic;

import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <b>ZoomEventhandler</b> provides event handlers for basic zooming
 * of the canvas view with the right (third) button.  The interaction is that
 * the initial mouse press defines the zoom anchor point, and then
 * moving the mouse to the right zooms with a speed proportional
 * to the amount the mouse is moved to the right of the anchor point.
 * Similarly, if the mouse is moved to the left, the the view is
 * zoomed out.
 * <P>
 * On a Mac with its single mouse button one may wish to change the
 * standard right mouse button zooming behavior. This can be easily done
 * with the PInputEventFilter. For example to zoom with button one and shift you
 * would do this:
 * <P>
 * <code>
 * <pre>
 * zoomEventHandler.getEventFilter().setAndMask(InputEvent.BUTTON1_MASK |
 *                                              InputEvent.SHIFT_MASK);
 * </pre>
 * </code>
 * <P>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class ExtendedZoomHandler extends edu.umd.cs.piccolo.event.PDragSequenceEventHandler {
	protected Point2D viewZoomPoint;
	protected double maxScale = Double.MAX_VALUE;
	protected double minScale =0;
	public final static double MARGIN_FACTOR = .01;

	/**
	 * Creates a new zoom handler.
	 */
	public ExtendedZoomHandler() {super();}

	/**Extended with double-click zoom extent.  Ctrl may or may be pressed (but does not need to be).
	 *
	 */
	public void mouseClicked(PInputEvent aEvent) {
		int onmask = 0;
	    int offmask = InputEvent.SHIFT_DOWN_MASK |  InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK;
		if (aEvent.getClickCount() == 2 && ((aEvent.getModifiersEx() & (onmask | offmask)) == onmask)) {
			PCamera camera = aEvent.getCamera();
			PBounds b = camera.getUnionOfLayerFullBounds();
			b.width= b.width +(2*MARGIN_FACTOR*b.width);
			b.height= b.height+(2*MARGIN_FACTOR*b.height);
			b.x = b.x - (MARGIN_FACTOR*b.width);
			b.y = b.y - (MARGIN_FACTOR*b.height);
			camera.animateViewToCenterBounds(b, true, 100);
		}
		super.mouseClicked(aEvent);
	}

	/**Changed to use ctrl as the modifier key for zoom handling and up/down instead of left/right.*/
	protected void dragActivityFirstStep(PInputEvent aEvent) {
		int onmask =  InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
	    int offmask = InputEvent.SHIFT_DOWN_MASK |InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK;
		if ((aEvent.getModifiersEx() & (onmask | offmask)) != onmask) {
			viewZoomPoint = null;
			return;
		}

		viewZoomPoint = aEvent.getPosition();
		super.dragActivityFirstStep(aEvent);
	}


	protected void dragActivityStep(PInputEvent aEvent) {
		if (viewZoomPoint == null) {return;}

		PCamera camera = aEvent.getCamera();
		double dy = aEvent.getCanvasPosition().getY() - getMousePressedCanvasPoint().getY();
		double scaleDelta = (1.0 + (0.001 * dy));

		double currentScale = camera.getViewScale();
		double newScale = currentScale * scaleDelta;

		if (newScale < minScale) {
			scaleDelta = minScale / currentScale;
		}
		if ((maxScale > 0) && (newScale > maxScale )) {
			scaleDelta = maxScale / currentScale;
		}

		camera.scaleViewAboutPoint(scaleDelta, viewZoomPoint.getX(), viewZoomPoint.getY());
	}
}
