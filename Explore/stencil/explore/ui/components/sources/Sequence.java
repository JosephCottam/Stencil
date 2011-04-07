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