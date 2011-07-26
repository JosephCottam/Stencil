package stencil.explore.ui.components.sources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import stencil.explore.model.sources.BinarySource;
import stencil.WorkingDir;

public class Binary extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	private final JButton fileList = new JButton("\u2026");
	protected final JTextField filename = new JTextField();
	protected final JFileChooser fileChooser;

	public Binary(BinarySource source) {
		this();
		set(source);
	}

	private Binary() {
		super("");

		fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new java.io.File(WorkingDir.get() + "input.txt"));

		final JPanel parent = this;
		fileList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String defaultFile = WorkingDir.resolve(filename.getText());

				if (!defaultFile.equals("")) {fileChooser.setSelectedFile(new java.io.File(defaultFile));}
				int rv = fileChooser.showOpenDialog(parent.getRootPane());
				if (rv == JFileChooser.APPROVE_OPTION) {
					String name;
					try {name = fileChooser.getSelectedFile().getCanonicalPath();}
					catch (Exception ex) {name = fileChooser.getSelectedFile().getAbsolutePath();}

					name = WorkingDir.relativize(name);
					filename.setText(name);
				}
				saveValues();
			}
		});

		this.add(labeledPanel("File: ", filename, fileList));
		


		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		filename.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public BinarySource get() {
		return new BinarySource(name, filename.getText());
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (BinarySource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		filename.setText(WorkingDir.relativize(source.filename()));
	}

}