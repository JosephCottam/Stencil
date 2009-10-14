package stencil.explore.ui.components.sources;

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
		
		this.add(labeledPanel("Fields: ", fields));
		this.add(labeledPanel("On Change ", onChange));
	}

	public void set(WindowStateSource source) {
		super.set(source);
		this.onChange.setSelected(source.onChange());
	}
	
	protected StreamSource get() {return new WindowStateSource(name, onChange.isSelected());}
}
