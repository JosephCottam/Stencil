package stencil.util.streams.ui;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;

public final class ComponentEventStream implements TupleStream {
	protected static final String X_FIELD = "X";
	protected static final String Y_FIELD = "Y";
	protected static final String WIDTH_FIELD = "WIDTH";
	protected static final String HEIGHT_FIELD = "HEIGHT";
	public static final String[] FIELDS = new String[]{X_FIELD, Y_FIELD, WIDTH_FIELD, HEIGHT_FIELD};

	/**Tuple to represent a state of a frame.*/
	private static final class ComponentState implements PrototypedTuple {
		private static final Class[] TYPES = new Class[]{int.class, int.class, int.class, int.class};
		private static final TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS, TYPES);
		public static final int X = PROTOTYPE.indexOf(X_FIELD);
		public static final int Y = PROTOTYPE.indexOf(Y_FIELD);
		public static final int WIDTH = PROTOTYPE.indexOf(WIDTH_FIELD);
		public static final int HEIGHT = PROTOTYPE.indexOf(HEIGHT_FIELD);
		
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		
		public ComponentState(Rectangle r) {
			this.x =r.x;
			this.y =r.y;
			this.width = r.width;
			this.height = r.height;
		}
		
		public Object get(String name) {return Tuples.namedDereference(name, this);}
		
		public Object get(int idx) {
			if (idx == X) {return x;}
			if (idx == Y) {return y;}
			if (idx == HEIGHT) {return height;}
			if (idx == WIDTH) {return width;}
			throw new TupleBoundsException(idx, size());
		}
		
		public int size() {return PROTOTYPE.size();}
		public TuplePrototype prototype() {return PROTOTYPE;}
	}
	
	/**Data source for frame status.*/
	private interface TupleSource {
		public Tuple next();
	}
	
	/**Data source that always returns the current state.*/
	private final class CurrentStateSource implements TupleSource {
		private final JComponent component;
		public CurrentStateSource(JComponent source) {
			this.component = source;
		}
		public Tuple next() {return new ComponentState(component.getBounds());}
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
		
		public ChangeStateSource(JComponent source) {
			source.addComponentListener(this);
		}
		
		public Tuple next() {
			Rectangle r = lastState.get();
			lastState.compareAndSet(r, null);			
			return new ComponentState(r);
		}

		public void componentMoved(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentResized(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentShown(ComponentEvent e) {lastState.set(e.getComponent().getBounds());}
		public void componentHidden(ComponentEvent e) {/*Event ignored.*/}
	}
	
	private final TupleSource tupleSource;
	private final String name;
	public ComponentEventStream(String name,JComponent source, boolean onChange) {
		assert source != null : "Cannot use a null frame";
		this.name = name;
		
		if (onChange) {
			tupleSource = new ChangeStateSource(source);
		} else {
			tupleSource = new CurrentStateSource(source);
		}
	}
	
	public SourcedTuple next() {return new SourcedTuple.Wrapper(name, tupleSource.next());}
	public boolean hasNext() {return true;}

	/**Throws UnsupportedOpertaionException.*/
	public void remove() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}

	/**Throws UnsupportedOpertaionException.*/
	public void close() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
}
