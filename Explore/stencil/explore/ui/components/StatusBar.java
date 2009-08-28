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
package stencil.explore.ui.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

public class StatusBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Font MESSAGE_FONT = new Font("Sans-serif", Font.PLAIN, 11);
	private JLabel message;
	private JLabel position;

	private String line;
	private String charNum;

	public StatusBar() {
		super();
		BorderLayout l = new BorderLayout();
		this.setLayout(l);

		message = new JLabel("Ready");
		message.setFont(MESSAGE_FONT);
		message.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));

		position = new JLabel("0:0");
		position.setFont(MESSAGE_FONT);
		position.setAlignmentY(Component.LEFT_ALIGNMENT);
		position.setMinimumSize(new Dimension(100, 20));
		position.setPreferredSize(new Dimension(100, 20));

		this.add(message, BorderLayout.CENTER);
		this.add(position, BorderLayout.EAST);

	}

	public void setLineNum(int line) {
		if (line <0) {this.line = "?";}
		else {this.line = Integer.toString(line);}
		updatePosition();
	}

	public void setCharNum(int charNum) {
		if (charNum <0) {this.charNum = "?";}
		else {this.charNum = Integer.toString(charNum);}
		updatePosition();
	}

	private void updatePosition() {
		position.setText(String.format("%1$s : %2$s", line, charNum));
		message.repaint();
	}

	public void setMessage(String text) {
		message.setText(text);
		this.repaint();
	}

	public void clearMessage() {
		message.setText("");
		message.repaint();
	}
}
