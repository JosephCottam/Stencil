package stencil.explore.ui.components.sources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import stencil.explore.model.sources.FileSource;
import stencil.WorkingDir;

public class File extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	private final JTextField size = new JTextField();
	private final JTextField skip = new JTextField();
	private final JCheckBox strict = new JCheckBox();
	private final JTextField separator = new JTextField();
	private final JButton fileList = new JButton("\u2026");
	protected final JTextField filename = new JTextField();
	protected final JFileChooser fileChooser;

	public File(FileSource source) {
		this();
		set(source);
	}

	private File() {
		super("");
		skip.setText("0");

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

		this.add(labeledPanel("Tuple Size: ", size));
		this.add(labeledPanel("Separator: ", separator));
		this.add(labeledPanel("File: ", filename, fileList));
		this.add(labeledPanel("Skip Lines: ", skip));
		this.add(labeledPanel("Strict: ", strict));
		


		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		size.addFocusListener(fl);
		separator.addFocusListener(fl);
		filename.addFocusListener(fl);
		skip.addFocusListener(fl);
		strict.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public FileSource get() {
		return new FileSource(name, Integer.parseInt(size.getText()), filename.getText(), separator.getText(), Integer.parseInt(skip.getText()), strict.isSelected());
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (FileSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		size.setText(Integer.toString(source.size()));
		strict.setSelected(source.strict());
		skip.setText(Integer.toString(source.skip()));
		separator.setText(source.separator());
		filename.setText(WorkingDir.relativize(source.filename()));
	}

}