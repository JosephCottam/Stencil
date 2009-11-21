package stencil.util.streams.ui;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.TupleStream;
import stencil.tuple.Tuples;

public final class ComponentEventStream implements TupleStream {
	public static final List<String> FIELDS = Arrays.asList("SOURCE", "X", "Y", "WIDTH", "HEIGHT");
	
	/**Tuple to represent a state of a frame.*/
	private static final class ComponentState implements Tuple {
		private static final String SOURCE_FIELD = "SOURCE";
		private static final String X_FIELD = "X";
		private static final String Y_FIELD = "Y";
		private static final String WIDTH_FIELD = "WIDTH";
		private static final String HEIGHT_FIELD = "HEIGHT";
		private static final List<String> PROTOTYPE = Arrays.asList(SOURCE_FIELD, X_FIELD, Y_FIELD, WIDTH_FIELD, HEIGHT_FIELD);
		public static final int SOURCE = PROTOTYPE.indexOf(SOURCE_FIELD);
		public static final int X = PROTOTYPE.indexOf(X_FIELD);
		public static final int Y = PROTOTYPE.indexOf(Y_FIELD);
		public static final int WIDTH = PROTOTYPE.indexOf(WIDTH_FIELD);
		public static final int HEIGHT = PROTOTYPE.indexOf(HEIGHT_FIELD);
		
		private final String source;
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		
		public ComponentState(String source, Rectangle r) {
			this.source =source;
			this.x =r.x;
			this.y =r.y;
			this.width = r.width;
			this.height = r.height;
		}
		
		public Object get(String name) {return Tuples.namedDereference(name, this);}
		
		public Object get(int idx) {
			if (idx == SOURCE) {return source;}
			if (idx == X) {return x;}
			if (idx == Y) {return y;}
			if (idx == HEIGHT) {return height;}
			if (idx == WIDTH) {return width;}
			throw new TupleBoundsException(idx, size());
		}
		
		public int size() {return PROTOTYPE.size();}
		public List<String> getPrototype() {return PROTOTYPE;}
		public boolean isDefault(String name, Object value) {return false;}		
	}
	
	/**Data source for frame status.*/
	private interface TupleSource {
		public boolean ready(); 
		public Tuple next();
	}
	
	/**Data source that always returns the current state.*/
	private final class CurrentStateSource implements TupleSource {
		private final JComponent component;
		private final String sourceName;
		public CurrentStateSource(String sourceName, JComponent source) {
			this.sourceName = sourceName;
			this.component = source;
		}
		public boolean ready() {return true;}
		public Tuple next() {return new ComponentState(sourceName, component.getBounds());}
	}
	
	/**Data source that only returns a tuple if it has changed.
	 * Values may be missed if polling for new states is not 
	 * sufficiently frequent.
	 * 
	 * @author jcottam
	 *
	 */
	private final class ChangeStateSource implements TupleSource, ComponentListener {
		private AtomicReference<Rectangle> lastState = new AtomicReference(null);
		private final String sourceName;
		
		public ChangeStateSource(String sourceName, JComponent source) {
			this.sourceName = sourceName;
			source.addComponentListener(this);
		}
		
		public Tuple next() {
			Rectangle r = lastState.get();
			lastState.compareAndSet(r, null);			
			return new ComponentState(sourceName, r);
		}

		public boolean ready() {return lastState.get() != null;}
		
		public void componentMoved(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentResized(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentShown(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentHidden(ComponentEvent e) {/*Event ignored.*/}
	}
	
	private final TupleSource tupleSource;

	public ComponentEventStream(String name, JComponent source, boolean onChange) {
		assert source != null : "Cannot use a null frame";
		if (onChange) {
			tupleSource = new ChangeStateSource(name, source);
		} else {
			tupleSource = new CurrentStateSource(name, source);
		}
	}
	
	public Tuple next() {return tupleSource.next();}
	public boolean ready() {return tupleSource.ready();}
	public boolean hasNext() {return true;}

	/**Throws UnsupportedOpertaionException.*/
	public void remove() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}

	/**Throws UnsupportedOpertaionException.*/
	public void close() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
}
