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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import stencil.explore.ui.interactive.Interactive;
import stencil.explore.model.sources.TextSource;

public final class Text extends stencil.explore.ui.components.sources.SourceEditor {
	private static final long serialVersionUID = -6217403692229831654L;

	private final JTextArea data;
	private final JTextField header;
	private final JTextField separator;

	public Text(TextSource source) {
		this();
		set(source);
	}

	private Text() {
		super("");
		header = new JTextField();
		separator = new JTextField();
		data = new JTextArea();

		setLayout(new BorderLayout());

		JPanel config = new JPanel();

		JPanel headerPanel = new JPanel();
		JLabel headerLabel = new JLabel("Header:");
		headerPanel.setLayout(new BorderLayout());
		headerPanel.add(headerLabel, BorderLayout.WEST);
		headerPanel.add(header, BorderLayout.CENTER);


		JPanel separatorPanel = new JPanel();
		JLabel seperatorLabel = new JLabel("Separator");
		separatorPanel.setLayout(new BorderLayout());
		separatorPanel.add(seperatorLabel, BorderLayout.WEST);
		separatorPanel.add(separator, BorderLayout.CENTER);

		Dimension size = new Dimension(separator.getPreferredSize().height, 20);
		separator.setPreferredSize(size);
		separator.setMinimumSize(size);
		separator.setMinimumSize(size);
		separator.setSize(size);

		config.setLayout(new BorderLayout());
		config.add(headerPanel, BorderLayout.CENTER);
		config.add(separatorPanel, BorderLayout.EAST);

		JScrollPane scroller = new JScrollPane(data, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		data.setFont(Interactive.styleFont(Interactive.PROGRAM_FONT_NAME));
		data.setTabSize(3);

		add(config, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);


		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		data.addFocusListener(fl);
		separator.addFocusListener(fl);
		header.addFocusListener(fl);
		this.addFocusListener(fl);
	}

	public void set(TextSource source) {
		assert source != null : "Cannot pass a null to 1-argument set.";
		super.set(source);
		header.setText(source.header());
		separator.setText(source.separator());
		data.setText(source.text());
	}

	public TextSource get() {
		return new TextSource(name, header.getText(), separator.getText(), data.getText());
	}

}
