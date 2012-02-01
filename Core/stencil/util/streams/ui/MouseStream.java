package stencil.util.streams.ui;

import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import stencil.display.Display;
import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.parser.ParseStencil;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;

@Description("Tracking of the position and state of the mouse cursor.")
@Stream(name="Mouse", spec="[freq:-1]")
public class MouseStream implements TupleStream {
	public static String NAME = "Mouse";
	
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
			currentCanvas = Display.view.viewToCanvas(currentCanvas); //correct for view transform

			storedEvent = event;
			sequence++;
		}

		public void mouseEntered(MouseEvent arg0) {/*No action taken on event.*/}
		public void mouseExited(MouseEvent arg0) {/*No action taken on event.*/}
	}

	/**Indicate to frequency setting that only changes are interesting.*/
	public static final int ON_CHANGE = -1;

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
	private static String PROTOTYPE_STRING = "(int X, int Y, int BUTTON, int SCREEN_X, int SCREEN_Y, int DELTA_X, int DELTA_Y, int CLICK_COUNT, boolean CTRL, booealn ALT, boolean SHIFT, boolean META, String TYPE)";
	public static final TuplePrototype<TupleFieldDef> PROTOTYPE;
	static {
		TuplePrototype proto = null;
		try {proto = ParseStencil.prototype(PROTOTYPE_STRING, false);}
		catch (Throwable e) {e.printStackTrace();}
		PROTOTYPE = proto;
	}
	
	
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

	
	public MouseStream(String name, TuplePrototype proto, Specializer spec, Object[] args) {
		this(findComponent(args));
		if (!proto.nameEqual(PROTOTYPE)) {throw new IllegalArgumentException("Must provide exact prototype for mouse: " + PROTOTYPE);}
	}
	
	private static final Component findComponent(Object[] args) {
		for (Object arg: args) {if (arg instanceof Component) {return (Component) arg;}}
		throw new Error("System must supply StencilPanel for mouse streamt o work...");
	}
	
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
	public SourcedTuple next() {
		Object[] values = new Object[PROTOTYPE.size()];

		synchronized(mouse) {
			if (!ready()) {return null;}
			long now = System.currentTimeMillis();
			if (priorTime > now-(1000/frequency)) {return null;}  //Since time is reported delta 1970, we don't have to worry about date roll-over (until we have an epic fail).
			priorTime = System.currentTimeMillis();

			sequence = mouse.sequence;

			values[PROTOTYPE.indexOf("X")] = mouse.currentCanvas.getX();
			values[PROTOTYPE.indexOf("Y")] = -mouse.currentCanvas.getY();
			values[PROTOTYPE.indexOf("BUTTON")] = mouse.storedEvent.getButton();
			values[PROTOTYPE.indexOf("DELTA_X")] = mouse.current.getX() - mouse.prior.getX();
			values[PROTOTYPE.indexOf("DELTA_Y")] = -(mouse.current.getY() - mouse.prior.getY());
			values[PROTOTYPE.indexOf("SCREEN_X")] = mouse.current.getX();
			values[PROTOTYPE.indexOf("SCREEN_Y")] = mouse.current.getY();
			values[PROTOTYPE.indexOf("CLICK_COUNT")] = mouse.storedEvent.getClickCount();
			values[PROTOTYPE.indexOf("CTRL")] = mouse.storedEvent.isControlDown();
			values[PROTOTYPE.indexOf("ALT")] = mouse.storedEvent.isAltDown();
			values[PROTOTYPE.indexOf("SHIFT")] = mouse.storedEvent.isShiftDown();
			values[PROTOTYPE.indexOf("META")] = mouse.storedEvent.isMetaDown();
			values[PROTOTYPE.indexOf("TYPE")] = Types.getType(mouse.storedEvent.getID()).toString();

			mouse.prior = mouse.current;
		}

		Tuple t= new PrototypedArrayTuple(PROTOTYPE, values);
		return new SourcedTuple.Wrapper(NAME, t);
	}

	/**Returns true, unless the display is headless.*/
	public boolean hasNext() {
		String prop = System.getProperty("java.awt.headless");
		return prop == null  || !prop.toUpperCase().equals("TRUE");
	}

	private boolean ready() {
		synchronized (mouse) {
			return hasNext() & !(frequency == ON_CHANGE && sequence == mouse.sequence || mouse.storedEvent == null);
		}
	}

	@Override
	public void stop() {source.removeMouseListener(mouse);}

	/**Throws UnsupportedOpertaionException.*/
	public void close() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
	/**Throws UnsupportedOpertaionException.*/
	public void remove() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
}
