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
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import stencil.explore.model.sources.MouseSource;
import stencil.explore.model.sources.StreamSource;
import stencil.util.enums.EnumUtils;
import stencil.util.streams.ui.MouseStream;

public final class Mouse extends SourceEditor {
	private static final long serialVersionUID = 2763272640758576637L;

	protected String name;
	protected int backupFreq = 30;

	protected final JTextField elements = new JTextField();
	protected final JTextField frequency = new JTextField();
	protected final JCheckBox onChange = new JCheckBox();

	public Mouse(String name) {
		super(name);
		String names = Arrays.toString(EnumUtils.allNames(MouseStream.Names.class).toArray());
		elements.setText(names.substring(1,names.length()-1).replace('\"', ' ') + " ");
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