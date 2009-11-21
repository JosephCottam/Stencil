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
package stencil.util.streams.ui;


import java.util.Arrays;

import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import stencil.parser.tree.View;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.util.enums.EnumUtils;

/**A way to track the mouse position/state at all points in time.
 *
 * @author jcottam
 */
public class MouseStream implements TupleStream {
	/**Internal utility for capturing mouse information.*/
	private static class Mouse implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
		/**Screen coordinate of the current event*/
		public Point2D current = new Point(0,0);
		/**Screen coordinate of the prior event*/
		public Point2D prior = new Point(0,0);
		/**Canvas coordinate of the current event.
		 * This may not agree with the location of event, as drag events report the source of the drag and not its current position.
		 **/
		public Point2D currentCanvas = new Point(0,0);
		/**Copy of the current event*/
		public MouseEvent storedEvent;

		int sequence=Integer.MIN_VALUE;

		public Mouse(Component source) {/*Takes argument so enter/levae can be handled in the future.*/}


		public synchronized void mouseReleased(MouseEvent event) {storedEvent = event; sequence++;}
		public synchronized void mousePressed(MouseEvent event) {storedEvent = event; sequence++;}
		public synchronized void mouseClicked(MouseEvent event) {storedEvent = event; sequence++;}
		public synchronized void mouseMoved(MouseEvent event) {registerMovement(event, false);}
		public synchronized void mouseDragged(MouseEvent event) {registerMovement(event, true);}

		private void registerMovement(MouseEvent event, boolean drag) {
			current = java.awt.MouseInfo.getPointerInfo().getLocation();

			//Drag events only report the source of the drag, so we have to calculate
			//the screen mouse coordinate based on the screen location and the offset of the mouse
			if (drag) {
				Point offset = event.getComponent().getLocationOnScreen();		//MouseInfo is in screen coords, so figure out where the window physically is.
				currentCanvas = new Point2D.Double(current.getX()-offset.x, current.getY() - offset.y);
			} else {currentCanvas = event.getPoint();}
			currentCanvas = View.global.viewToCanvas(currentCanvas); //correct for view transform

			storedEvent = event;
			sequence++;
		}

		public void mouseEntered(MouseEvent arg0) {/*No action taken on event.*/}
		public void mouseExited(MouseEvent arg0) {/*No action taken on event.*/}
	}

	/**Indicate to frequency setting that only changes are interesting.*/
	public static final int ON_CHANGE = Integer.MIN_VALUE;

	/**How often should the mouse values be updated?  This should be expressed as updates per second.*/
	public static int frequency = ON_CHANGE;

	/**Values in the mouse tuple
	 *
	 * X,Y: Canvas position of the mouse
	 * BUTTONS: Which button(s) were pressed
	 * CLICK_COUNT: How many times was the button pressed
	 * SCREEN_X, SCREEN_Y: Screen position of the mouse
	 * DELTA_X, DELTA_Y: Amount of motion of the mouse since the last tuple (in screen coordinates)
	 * CTRL, ALT, SHIFT, META: Modifier keys concurrently pressed
	 * TYPE: Click/Press/Move/Drag
	 */
	public static enum Names {X, Y, BUTTON, SCREEN_X, SCREEN_Y, DELTA_X, DELTA_Y, CLICK_COUNT, CTRL, ALT, SHIFT, META, TYPE};
	public static enum Types {CLICK, PRESS, RELEASE, MOVE, DRAG;

		/**Translate an event.id to a valid event type*/
		public static Enum getType(int id) {
			if (id == MouseEvent.MOUSE_CLICKED) {return CLICK;}
			if (id == MouseEvent.MOUSE_MOVED) {return MOVE;}
			if (id == MouseEvent.MOUSE_PRESSED) {return PRESS;}
			if (id == MouseEvent.MOUSE_RELEASED) {return RELEASE;}
			if (id == MouseEvent.MOUSE_DRAGGED) {return DRAG;}
			throw new IllegalArgumentException("No mapping know for mouse-event id of " + id);
		}
	}

	protected Mouse mouse;
	protected Component source;
	protected int sequence =0;
	protected long priorTime;

	/**Mouse stream adapter, must have a component that roots its mouse events.
	 * It is suggested that the root Stencil Panel be passed in as the listened to component.
	 *
	 *  NOTE: This may not work if the root component doesn't get events for its children.
	 *
	 * @param source
	 */
	public MouseStream(Component source) {
		mouse = new Mouse(source);
		source.addMouseListener(mouse);
		source.addMouseMotionListener(mouse);
	}

	/**What is the current mouse state?*/
	public Tuple next() {
		Object[] values = new Object[Names.values().length];

		synchronized(mouse) {
			if (!ready()) {return null;}
			long now = System.currentTimeMillis();
			if (priorTime > now-(1000/frequency)) {return null;}  //Since time is reported delta 1970, we don't have to worry about date roll-over (until we have an epic fail).
			priorTime = System.currentTimeMillis();

			sequence = mouse.sequence;

			values[Names.X.ordinal()] = mouse.currentCanvas.getX();
			values[Names.Y.ordinal()] = mouse.currentCanvas.getY();
			values[Names.BUTTON.ordinal()] = mouse.storedEvent.getButton();
			values[Names.DELTA_X.ordinal()] = mouse.current.getX() - mouse.prior.getX();
			values[Names.DELTA_Y.ordinal()] = mouse.current.getY() - mouse.prior.getY();
			values[Names.SCREEN_X.ordinal()] = mouse.current.getX();
			values[Names.SCREEN_Y.ordinal()] = mouse.current.getY();
			values[Names.CLICK_COUNT.ordinal()] = mouse.storedEvent.getClickCount();
			values[Names.CTRL.ordinal()] = mouse.storedEvent.isControlDown();;
			values[Names.ALT.ordinal()] = mouse.storedEvent.isAltDown();
			values[Names.SHIFT.ordinal()] = mouse.storedEvent.isShiftDown();
			values[Names.META.ordinal()] = mouse.storedEvent.isMetaDown();
			values[Names.TYPE.ordinal()] = Types.getType(mouse.storedEvent.getID()).toString();

			mouse.prior = mouse.current;
		}
		Tuple t= new PrototypedTuple("Mouse", EnumUtils.allNames(Names.class), Arrays.asList(values));
		return t;
	}

	/**Returns true, unless the display is headless.*/
	public boolean hasNext() {
		String prop = System.getProperty("java.awt.headless");
		return prop == null  || !prop.toUpperCase().equals("TRUE");
	}

	public boolean ready() {
		return hasNext() & !(frequency == ON_CHANGE && sequence == mouse.sequence || mouse.storedEvent == null);
	}

	/**Throws UnsupportedOpertaionException.*/
	public void close() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
	/**Throws UnsupportedOpertaionException.*/
	public void remove() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}

}
