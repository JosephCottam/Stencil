package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import stencil.explore.model.sources.RandomSource;

public class Random extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	private final JTextField size = new JTextField();
	private final JTextField length = new JTextField();

	public Random(RandomSource source) {
		this();
		set(source);
	}

	private Random() {
		super("");
		size.setText("2");
		length.setText("-1");

		this.add(labeledPanel("Size: ", size));
		this.add(labeledPanel("Length: ", length));

		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		size.addFocusListener(fl);
		length.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public RandomSource get() {
		return new RandomSource(name, Integer.parseInt(size.getText()), Long.parseLong(length.getText()));
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (RandomSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		size.setText(Integer.toString(source.size()));
		length.setText(Long.toString(source.length()));
	}

}