package stencil.explore.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.PrintStream;

import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;
import javax.swing.ListCellRenderer;
 
import stencil.explore.util.MessageReporter;
import stencil.explore.util.ReporterAsStream;

/**Special panel that can be used display the messages of System.out or System.err in a
 *  window.
 */
public class MessagePanel extends JPanel implements MessageReporter {
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
		messages.setModel(new DefaultListModel()); 
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

	public synchronized void clear() {
		messages.setModel(new DefaultListModel()); 
	}

	
	public synchronized void addMessage(String message, Object...args) {
		if (message == null) {throw new RuntimeException(String.format("Null message add requested (args are %1$s).", Arrays.deepToString(args)));}
		
		if (args.length >0) {addMessage(String.format(message,args), false);}
		else{addMessage(message, false);}
	}
	
	public synchronized void addError(String message, Object...args) {
		if (message == null) {addMessage("Error occured.  Please check logs.", true); return;}
		
		if (args.length >0) {addMessage(String.format(message,args), true);}
		else {addMessage(message, true);}
	}

	
	private class MessageUpdate implements Runnable {
		private final String message;
		private final boolean error;
		
		public MessageUpdate(String message, boolean error) {
			this.message = message;
			this.error = error;
		}
		
		public void run() {
			Entry e = new Entry(message, error);
			((DefaultListModel) messages.getModel()).addElement(e);
			if (error) {System.err.println(message);}
			else {System.out.println(message);}			
		}
		
	}
	
	protected synchronized void addMessage(String message, boolean error) {
		javax.swing.SwingUtilities.invokeLater(new MessageUpdate(message, error));
	}

	public PrintStream errorAsStream() {
		return new PrintStream(new ReporterAsStream(this));
	}
}
