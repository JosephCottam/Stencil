/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import stencil.WorkingDirectory;

public class File extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	private final JTextField header = new JTextField();;
	private final JTextField skip = new JTextField();;
	private final JCheckBox strict = new JCheckBox();;
	private final JTextField separator = new JTextField();;
	private final JButton fileList = new JButton("\u2026");;
	protected final JTextField filename = new JTextField();;
	protected final JFileChooser fileChooser;

	public File(FileSource source) {
		this();
		set(source);
	}

	private File() {
		super("");
		skip.setText("0");

		fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new java.io.File(WorkingDirectory.getWorkingDir() + "input.txt"));

		final JPanel parent = this;
		fileList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String defaultFile = WorkingDirectory.resolvePath(filename.getText());

				if (!defaultFile.equals("")) {fileChooser.setSelectedFile(new java.io.File(defaultFile));}
				int rv = fileChooser.showOpenDialog(parent.getRootPane());
				if (rv == JFileChooser.APPROVE_OPTION) {
					String name;
					try {name = fileChooser.getSelectedFile().getCanonicalPath();}
					catch (Exception ex) {name = fileChooser.getSelectedFile().getAbsolutePath();}

					name = WorkingDirectory.relativePath(name);
					filename.setText(name);
				}
				saveValues();
			}
		});

		this.add(labeledPanel("Header: ", header));
		this.add(labeledPanel("Separator: ", separator));
		this.add(labeledPanel("File: ", filename, fileList));
		this.add(labeledPanel("Skip Lines: ", skip));
		this.add(labeledPanel("Strict: ", strict));
		


		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		header.addFocusListener(fl);
		separator.addFocusListener(fl);
		filename.addFocusListener(fl);
		skip.addFocusListener(fl);
		strict.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public FileSource get() {
		return new FileSource(name, filename.getText(), header.getText(), separator.getText(), Integer.parseInt(skip.getText()), strict.isSelected());
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (FileSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		header.setText(source.header());
		strict.setSelected(source.strict());
		skip.setText(Integer.toString(source.skip()));
		separator.setText(source.separator());
		filename.setText(WorkingDirectory.relativePath(source.filename()));
	}

}