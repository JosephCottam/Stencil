package stencil.util.streams.ui;

import javax.swing.JComponent;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.SourcedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class KeyboardStream implements TupleStream {

	/**KeysTuple encapsulates keyboard events by representing the primary key and modifier keys
	 * pressed in the form of a Tuple.  If a plain text needs to be represented character-by-character,
	 * using a modifier field of 0 and the raw characters will suffice.
	 */
	public static class KeysTuple implements PrototypedTuple {
		private static final String KEY_FIELD = "key";
		private static final String MODIFIER_FIELD="modifier";
		private static final String[] FIELDS = new String[]{KEY_FIELD, MODIFIER_FIELD};
		private static final Class[] TYPES = new Class[]{Character.class, Integer.class};
		private static final TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS, TYPES);
		public static final int KEY = Arrays.asList(FIELDS).indexOf(KEY_FIELD); 
		public static final int MODIFIER = Arrays.asList(FIELDS).indexOf(MODIFIER_FIELD);

		private Character key = null;
		private Integer modifiers = null;

		public KeysTuple(Character key, Integer modifiers) {
			this.key = key;
			this.modifiers = modifiers;
		}

		public Object get(String name) {return Tuples.namedDereference(name, this);}
		
		public int size() {return PROTOTYPE.size();}
		public Object get(int idx) {
			if (idx == KEY) {return key;}
			if (idx == MODIFIER) {return modifiers;}
			throw new TupleBoundsException(idx, size());
 		}

		public TuplePrototype prototype() {return PROTOTYPE;}

		public boolean hasField(String name) {
			name = name.toUpperCase();
			return name.equals(KEY_FIELD) || name.equals(MODIFIER_FIELD);
		}

		public boolean isDefault(String name, Object value) {return false;}
	}


	/**Wrapper for an InputStream.  Supports naming of the incoming stream.  Inputs are wrapped
	 * in KeysTuple objects with no modifiers.  End-of-stream is represented as a null.  All exceptions
	 * encountered in reading are re-thrown.
	 */
	private static class InputStreamWrapper extends KeyboardStream {
		private DataInputStream stream;
		private String source;
		private char buffer =(char)-1;

		public InputStreamWrapper(InputStream stream, String source) {this.stream=new DataInputStream(stream); this.source=source;}
		private KeysTuple read() {
			try {
				if (buffer == -1) {buffer = stream.readChar();}
				if (buffer == -1) {return null;}
				buffer = stream.readChar();
				return new KeysTuple(buffer, 0);
			} catch (Exception e) {
				throw new NoSuchElementException("InputStream identified as " + source + " has reached its end (is returning -1)");
			}
		}

		public boolean hasNext() {return buffer != -1;}
		public void stop() {
			try {stream.close();}
			catch(Exception e) {throw new RuntimeException("Error closing buffer.", e);}
			finally {buffer=(char)-1;}
		}
		
		public SourcedTuple next() {return new SourcedTuple.Wrapper(source, read());}
	}

	/**Turns an component into an input stream.  Key events are buffered and dispensed
	 * upon request.
	 */
	private static class ComponentWrapper extends KeyboardStream implements KeyListener{
		private JComponent control;
		private String source;
		private List<KeysTuple> buffer;

		public ComponentWrapper(JComponent control, String source) {
			this.source =source;
			this.control = control;
			control.addKeyListener(this);
			buffer = new ArrayList<KeysTuple>();
		}

		public void keyPressed(KeyEvent arg0) {/*No action taken on event.*/}
		public void keyReleased(KeyEvent arg0) {/*No action taken on event.*/}
		public void keyTyped(KeyEvent evt) {
			KeysTuple t = new KeysTuple(evt.getKeyChar(), evt.getModifiers());
			buffer.add(t);
		}

		public boolean hasNext() {return buffer.size()>0;}

		public SourcedTuple next() {
			KeysTuple t = buffer.get(0);
			buffer.remove(0);
			return new SourcedTuple.Wrapper(source, t);
		}

		public void stop() {
			control.removeKeyListener(this);
			buffer.clear();
		}
	}


	public static KeyboardStream make(Object o, String name) {
		Class<? extends Object> type = o.getClass();
		if (JComponent.class.isAssignableFrom(type)) {
			 return make((JComponent) o, name);
		} else if (InputStream.class.isAssignableFrom(type)) {
			 return make((InputStream) o, name);
		}else {
			throw new ClassCastException("Can only construct KeyboardStreams from InputStream or JComponent derived objects.");
		}
	}
	public static KeyboardStream make(InputStream stream, String name) {return new InputStreamWrapper(stream, name);}
	public static KeyboardStream make(JComponent control, String source) {return new ComponentWrapper(control, source);}

	public void reset() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
	public void remove() {throw new UnsupportedOperationException(this.getClass().getName() +" does not support " + Thread.currentThread().getStackTrace()[0].getMethodName() + ".");}
	
	/**Close stream, disposing of relevant resources.*/
	public abstract void stop();
}
