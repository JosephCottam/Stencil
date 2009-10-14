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
package stencil.explore.ui.components;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.ListCellRenderer;
 
import stencil.explore.util.MessageReporter;

/**Special panel that can be used display the messages of System.out or System.err in a
 *  window.
 */
public class MessagePanel extends JPanel implements MessageReporter {
	private static final PrintStream ERR = System.err;
	private static final PrintStream OUT = System.out;

	/**Should messages added to this panel also be echoed on the corresponding System stream?*/
	public static boolean ECHO = true;
	
	protected JList messages;
	protected JButton clear;

	private class ElementRenderer extends JTextArea implements ListCellRenderer {
		public ElementRenderer() {
			super();
			this.setEditable(false);
			this.setTabSize(1);
		}
		
		public Component getListCellRendererComponent(JList list, Object element, int idx, boolean selected, boolean focused) {
			String text = "";
			Color foreground = Color.BLACK;
			if (element == null) {
				//intentionally blank
			} else if (!(element instanceof Entry)) {
				text = element.toString();
				foreground = Color.BLACK;
			} else {
				Entry e = (Entry) element;
				text = e.message;
				if (e.error) {foreground = java.awt.Color.RED.darker();}
			}
			this.setText(text);
			this.setForeground(foreground);

			return this;
		}
	}

	private class Entry {
		boolean error;
		String message;
		public Entry(String message, boolean err) {
			this.message=message;
			this.error = err;
		}
	}

	public MessagePanel() {
		messages = new JList();
		messages.setCellRenderer(new ElementRenderer());

		clear = new JButton("Clear");

		JPanel clearPanel = new JPanel();
		clearPanel.add(clear);

		JScrollPane errorScroller = new JScrollPane(messages);

		this.setLayout(new BorderLayout());
		this.add(errorScroller, BorderLayout.CENTER);
		this.add(clearPanel, BorderLayout.SOUTH);

		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {clear();}
		});

		clear();
	}

	public void clear() {
		messages.setModel(new DefaultListModel()); 
	}

	
	public void addMessage(String message, Object...args) {
		if (message == null) {throw new RuntimeException(String.format("Null message add requested (args are %1$s).", Arrays.deepToString(args)));}
		
		if (args.length >0) {addMessage(String.format(message,args), false);}
		else{addMessage(message, false);}
	}
	public void addError(String message, Object...args) {
		if (message == null) {addMessage("Error occured.  Please check logs.", true); return;}
		
		if (args.length >0) {addMessage(String.format(message,args), true);}
		else {addMessage(message, true);}
	}

	protected void addMessage(String message, boolean error) {
		final String mssg = message;
		message = message.trim();

		Entry e = new Entry(mssg, error);
		((DefaultListModel) messages.getModel()).addElement(e);
	}

	/**Create a link to this messagePanel that is a PrintStream.
	 * NOTE: Doesn't always do the right thing with newlines...but its getting better.
	 */
	public PrintStream streamLink(boolean error) {
		final boolean err = error;
		final OutputStream echo = err ? ERR : OUT;

		java.io.OutputStream out = new OutputStream()
		{
			public String buffer = "";
			public void write(byte[] b) throws IOException {
				if (ECHO) {echo.write(b);}
				bufferMessage(String.valueOf(new String(b)));
			}
			public void write(int b) throws IOException {
				if (ECHO) {echo.write(b);}
				bufferMessage(String.valueOf((char) b));
			}
			public void write(byte[] b, int off, int len) throws IOException {
				if (ECHO) {echo.write(b, off, len);}
				bufferMessage(new String(b, off, len));
			}

			private void bufferMessage(String message) {
				buffer = buffer + message;
				if (buffer.contains("\n")) {
					addMessage(buffer, err);
					buffer = "";
				}

			}
		};

		return new PrintStream(out, true);
	}

}
