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

import javax.swing.JComponent;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.TupleStream;
import stencil.tuple.Tuples;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
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
	public static class KeysTuple implements Tuple {
		private static final String KEY_FIELD = "key";
		private static final String MODIFIER_FIELD="modifier";
		private static final String SOURCE_FIELD = "source";
		private static final List<String> PROTOTYPE = Arrays.asList(KEY_FIELD, MODIFIER_FIELD, SOURCE_FIELD);
		public static final int KEY = PROTOTYPE.indexOf(KEY_FIELD);
		public static final int MODIFIER = PROTOTYPE.indexOf(MODIFIER_FIELD);
		public static final int SOURCE = PROTOTYPE.indexOf(SOURCE_FIELD);

		private static List<String> fields = null;
		private Character key = null;
		private Integer modifiers = null;
		private String source = null;

		public KeysTuple(Character key, Integer modifiers, String source) {
			this.key = key;
			this.modifiers = modifiers;
			this.source = source;
		}

		public Object get(String name) {return Tuples.namedDereference(name, this);}
		
		public int size() {return PROTOTYPE.size();}
		public Object get(int idx) {
			if (idx == KEY) {return key;}
			if (idx == MODIFIER) {return modifiers;}
			if (idx == SOURCE) {return source;}
			throw new TupleBoundsException(idx, size());
 		}

		public String getSource() {return source;}
		public void setSource(String source) {this.source = source;}

		public List<String> getPrototype() {
			if (fields == null) {
				fields = new ArrayList<String>();
				fields.add(KEY_FIELD);
				fields.add(MODIFIER_FIELD);
			}
			return fields;
		}

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
				return new KeysTuple(buffer, 0, source);
			} catch (Exception e) {
				throw new NoSuchElementException("InputStream identified as " + source + " has reached its end (is returning -1)");
			}
		}

		public boolean hasNext() {return buffer != -1;}
		public void close() throws IOException {stream.close(); buffer=(char)-1;}
		public Tuple next() {return read();}
		public boolean ready() {return buffer >0;}
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
			KeysTuple t = new KeysTuple(evt.getKeyChar(), evt.getModifiers(), source);
			buffer.add(t);
		}

		public boolean hasNext() {return buffer.size()>0;}

		public KeysTuple next() {
			KeysTuple t = buffer.get(0);
			buffer.remove(0);
			return t;
		}

		public void close() {
			control.removeKeyListener(this);
			buffer.clear();
		}

		public boolean ready() {return buffer.size() >0;}
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
	public abstract void close() throws Exception;
}
