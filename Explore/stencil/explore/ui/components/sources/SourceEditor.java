package stencil.explore.ui.components.sources;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import stencil.explore.model.sources.SourceCache;
import stencil.explore.model.sources.StreamSource;

/**Root of the source editors.  Provides default functionality and consistently look,
 * as well as utlities to preserve the consistent look.
 * 
 * Defaults:
 * 		Default layout is Box Layout along Y-axis
 * 
 * @author jcottam
 *
 */
@SuppressWarnings("serial")
public abstract class SourceEditor extends JPanel {
	protected String name;
	
	protected final JCheckBox delay = new JCheckBox();
	
	public SourceEditor(String name) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.name =name;
		this.add(labeledPanel("Delay: ", delay));
	}
	
	protected void set(StreamSource source) {
		this.name = source.name();
		delay.setSelected(source.delay());
		
		delay.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		});

	}
	protected abstract StreamSource get();
	
	protected void saveValues() {
		SourceCache.put(get());
		fireChangeEvent();
	}
	
	protected void fireChangeEvent() {
		ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
		for (int i = 0 ; i < listeners.length; i++) {
			ChangeEvent e =new ChangeEvent(this);
			listeners[i].stateChanged(e);
		}
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	

	/**Create a panel containing the label and the component.  
	 * Will apply layout rules according to the component type.
	 * @param label
	 * @param body
	 * @return
	 */
	protected static JPanel labeledPanel(String label, JComponent body) {return labeledPanel(label, body, null);}
	protected static JPanel labeledPanel(String label, JComponent body, JComponent suffix) {
		JPanel p = new JPanel();
		JLabel l = new JLabel(label);
		
		if (body instanceof JTextField) {p.setLayout(new BorderLayout());}
		
		p.add(l, BorderLayout.WEST);
		p.add(body, BorderLayout.CENTER);
		
		if (suffix != null) {
			p.add(suffix, BorderLayout.EAST);
		}

		return p;
	}


}
