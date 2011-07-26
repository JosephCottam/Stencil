package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import stencil.explore.model.sources.SequenceSource;

public class Sequence extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	private final JTextField start = new JTextField();
	private final JTextField increment = new JTextField();
	private final JTextField stop = new JTextField();

	public Sequence(SequenceSource source) {
		this();
		set(source);
	}

	private Sequence() {
		super("");
		start.setText("0");
		increment.setText("1");
		stop.setText("10");

		this.add(labeledPanel("Start: ", start));
		this.add(labeledPanel("Increment: ", increment));
		this.add(labeledPanel("Stop: ", stop));

		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		start.addFocusListener(fl);
		stop.addFocusListener(fl);
		increment.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public SequenceSource get() {
		return new SequenceSource (name, Double.parseDouble(start.getText()), Double.parseDouble(increment.getText()), Double.parseDouble(stop.getText()));
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (SequenceSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		start.setText(Double.toString(source.start()));
		increment.setText(Double.toString(source.increment()));
		stop.setText(Double.toString(source.stop()));
	}

}