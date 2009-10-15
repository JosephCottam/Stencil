package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import stencil.explore.model.sources.StreamSource;
import stencil.explore.model.sources.WindowStateSource;

public class WindowState extends SourceEditor {

	protected JCheckBox onChange = new JCheckBox();
	protected JTextField fields = new JTextField();
	
	public WindowState(WindowStateSource source) {
		this();
		set(source);
	}
	
	public WindowState() {
		super("");
		fields.setText(WindowStateSource.HEADER);
		fields.setEditable(false);
		
		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		onChange.addFocusListener(fl);
		fields.addFocusListener(fl);
		
		this.add(labeledPanel("Fields: ", fields));
		this.add(labeledPanel("On Change ", onChange));
	}

	public void set(WindowStateSource source) {
		super.set(source);
		this.onChange.setSelected(source.onChange());
	}
	
	protected StreamSource get() {
		return new WindowStateSource(name, onChange.isSelected());
	}
}
