package stencil.util.streams.ui;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

public final class ComponentEventStream implements TupleStream {
	public static final List<String> FIELDS = Arrays.asList("SOURCE", "X", "Y", "WIDTH", "HEIGHT");
	
	/**Tuple to represent a state of a frame.*/
	private static final class ComponentState implements Tuple {
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
		
		public Object get(String name) throws InvalidNameException {
			if (name.equals("SOURCE")) {return source;}
			if (name.equals("X")) {return x;}
			if (name.equals("Y")) {return y;}
			if (name.equals("WIDTH")) {return width;}
			if (name.equals("HEIGHT")) {return height;}
			
			throw new InvalidNameException(name, FIELDS);
		}

		public List<String> getPrototype() {return FIELDS;}
		public boolean hasField(String name) {return FIELDS.contains(name);}
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
