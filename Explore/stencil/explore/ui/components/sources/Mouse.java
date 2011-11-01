package stencil.explore.ui.components.sources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import stencil.explore.model.sources.MouseSource;
import stencil.explore.model.sources.StreamSource;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.util.streams.ui.MouseStream;
import stencil.util.collections.ArrayUtil;

public final class Mouse extends SourceEditor {
	private static final long serialVersionUID = 2763272640758576637L;

	protected int backupFreq = 30;

	protected final JTextField elements = new JTextField();
	protected final JTextField frequency = new JTextField();
	protected final JCheckBox onChange = new JCheckBox();

	public Mouse(String name) {
		super(name);
		String names = ArrayUtil.prettyString(TuplePrototypes.getNames(MouseStream.PROTOTYPE));
		elements.setText(names);
		elements.setEditable(false);


		this.add(labeledPanel("Header: ", elements));
		this.add(labeledPanel("Updates per second: ", frequency));
		this.add(labeledPanel("On Change ", onChange));

		if (MouseStream.frequency== MouseStream.ON_CHANGE) {
			frequency.setText("N/A");
			frequency.setEnabled(false);
			onChange.setSelected(true);
		}else{
			frequency.setText(Integer.toString(MouseStream.frequency));
			onChange.setSelected(false);
		}

		onChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (onChange.isSelected()) {
					frequency.setEnabled(false);
					try {backupFreq = Integer.parseInt(frequency.getText());}
					catch (Exception ex) {backupFreq = 30;}
					frequency.setText("N/A");
				} else {
					frequency.setText(Integer.toString(backupFreq));
					frequency.setEnabled(true);
				}
			}
		});

		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent e) {/*No action.*/}
			public void focusLost(FocusEvent e) {saveValues();}
		};

		onChange.addFocusListener(fl);
		frequency.addFocusListener(fl);
	}

	protected void saveValues() {
		int oldFrequency = backupFreq;
		if (onChange.isSelected()) {oldFrequency = MouseStream.ON_CHANGE;}
		MouseStream.frequency = oldFrequency;
		super.saveValues();
		this.fireChangeEvent();
	}

	@Override
	protected StreamSource get() {return new MouseSource(name);}

}