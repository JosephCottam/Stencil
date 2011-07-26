package stencil.explore.ui.components;

import static stencil.explore.Application.reporter;
import stencil.explore.ui.interactive.Interactive;
import stencil.explore.ui.components.events.TextPositionChangedListener;
import stencil.explore.coordination.*;
import stencil.parser.ParseStencil;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class StencilEditorPanel extends JScrollPane implements StencilMutable.Stencil {
	protected JTextArea stencilSource = new JTextArea();
	protected UndoManager undoManager = new UndoManager();
	protected UndoableEditListener undoListener;

	protected Exception priorException;	//Used to reduce reporting traffic
	
	/**Flag to indicate when an internal update is occurring.
	 * Suspends some events from being processed.*/
	protected boolean internalUpdate = false;

	public StencilEditorPanel() {
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(stencilSource);

		stencilSource.setFont(Interactive.styleFont(Interactive.PROGRAM_FONT_NAME));
		stencilSource.setTabSize(3);

		//Line/char number reporting
		stencilSource.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				int offset = stencilSource.getCaretPosition();
				int line =-1;
				int charNum =-1;
				try {
					line =stencilSource.getLineOfOffset(offset);
					charNum = offset -stencilSource.getLineStartOffset(line);
				} catch (Exception e) {/*Ignore*/}

				//TODO:Handle the fact that tabs are on character, but multiple spaces
				fireTextPositionChanged(line, charNum);
			}

		});

		
		//Semi-Continuous compile (at least, whenever the Stencil changes)
		stencilSource.addCaretListener(new CaretListener() {
			String oldText = "";
			
			public void caretUpdate(CaretEvent arg0) {
				if (internalUpdate) {return;}

				try {
					if (stencilSource.getText().equals(oldText)) {return;}
					oldText = stencilSource.getText();
					stencilChanged(); //Update all self state before telling others things have changed
					fireStencilChangedEvent();
				} catch (Exception e) {
					reporter.addError("Caret Update Error: %1$s", e.getMessage());
					e.printStackTrace(); 
				}
			}
		});

		//Link/prepare undo/redo management
		undoListener = new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		};
		stencilSource.getDocument().addUndoableEditListener(undoListener);
	}

	public JMenu getEditMenu() {
		final JMenuItem undo = new JMenuItem("Undo");
		undo.setMnemonic(KeyEvent.VK_Z);
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK));

		final JMenuItem redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));

		JMenu edit = new JMenu("Edit");
		edit.add(undo);
		edit.add(redo);

		ActionListener editMenuListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					if (e.getSource() == undo) {
						undoManager.undo();
					} else if (e.getSource() == redo) {
						undoManager.redo();
					} else {System.err.println("Edit menu event not handed.");}
				} catch (Exception ex) {/*Errors ignored in undo.*/}
			}

		};

		undo.addActionListener(editMenuListener);
		redo.addActionListener(editMenuListener);
		return edit;
	}

	public void setStencil(String text) {
		if (stencilSource.getText().equals(text)) {return;}
		internalUpdate = true;

		stencilSource.setText(text);
		stencilSource.setCaretPosition(0);
		stencilChanged();

		undoManager.discardAllEdits(); //Clear out undo information
		fireStencilChangedEvent();
		internalUpdate = false;
	}

	public String getStencil() {return stencilSource.getText();}

	public void addTextPositionChangedListener(TextPositionChangedListener l) {listenerList.add(TextPositionChangedListener.class, l);}
	public void fireTextPositionChanged(int line, int charNum) {
		TextPositionChangedListener[] listeners = listenerList.getListeners(TextPositionChangedListener.class);
		for (int i = 0 ; i < listeners.length; i++) {
			listeners[i].textPositionChanged(line, charNum);
		}
	}

	public void addStencilChangedListener(StencilListener.StencilChanged l) {listenerList.add(StencilListener.StencilChanged.class, l);}
	
	public void fireStencilChangedEvent() {
		StencilListener.StencilChanged[] listeners = listenerList.getListeners(StencilListener.StencilChanged.class);
		StencilEvent.StencilChanged update = new StencilEvent.StencilChanged(this, stencilSource.getText());
		for (int i = 0 ; i < listeners.length; i++) {
			listeners[i].stencilChanged(update);
		}
	}

	public void setSuccessIndicator(boolean success) {
		if (success) {
			Color base= UIManager.getColor("TextArea.background");
			setViewportBorder(BorderFactory.createLineBorder(base, 5));
		}else {
			Color base= UIManager.getColor("TextArea.selectionBackground");
			Color c = new Color(base.getBlue(), base.getRed(),base.getGreen());
			setViewportBorder(BorderFactory.createLineBorder(c, 5));
		}
		repaint();
	}

	public void stencilChanged() {
		try {
//			ParseStencil.program(stencilSource.getText(), stencil.adapters.java2D.Adapter.ADAPTER);
			ParseStencil.checkParse(stencilSource.getText());
			reporter.clear();
		} catch (Exception e) {
			//Do not report if this message is identical to the prior message
			if (priorException!=null && e.getMessage().equals(priorException.getMessage())) {return;}
			priorException =e;
			
			setSuccessIndicator(false);
			reporter.clear();
			reporter.addError(e.getMessage());
			e.printStackTrace();
			return;
		}
		priorException = null;
		setSuccessIndicator(true);
	}


}
