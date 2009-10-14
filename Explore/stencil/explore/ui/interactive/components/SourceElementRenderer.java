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
package stencil.explore.ui.interactive.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import stencil.explore.model.sources.StreamSource;
import stencil.explore.ui.interactive.Interactive;

/**Render stream sources in a list.  Colors the background
 * according to the amount of data required before the stream
 * source can be used to feed a stencil.
 *
 * @author jcottam
 *
 */
public class SourceElementRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = -5220540402142484409L;
	private static Color NOT_READY;
	private static Color NOT_READY_SELECTED;

	private JLabel name = new JLabel();
	private JLabel type = new JLabel();
	public SourceElementRenderer() {
		this.setOpaque(true);
		this.setLayout(new BorderLayout());

		this.add(name, BorderLayout.CENTER);
		this.add(type, BorderLayout.EAST);

		name.setFont(Interactive.styleFont(name.getFont()));
		type.setFont(Interactive.styleFont(type.getFont()));
	}

	public Component getListCellRendererComponent(JList list, Object element, int idx, boolean selected, boolean focused) {
		StreamSource e = (StreamSource) element;

		name.setText(e.name());
		type.setText("(" + e.getTypeName().substring(0,1) + ")");

		if (!e.isReady() && selected) {this.setBackground(getNotReadySelectedColor(list));}
		else if (!e.isReady()) {this.setBackground(getNotReadyColor(list));}
		else if (selected) {this.setBackground(list.getSelectionBackground());}
		else {
			this.setBackground(UIManager.getColor("JList.background"));
		}
		return this;
	}

	private Color getNotReadySelectedColor(JList list) {
		if (NOT_READY_SELECTED == null) {
			NOT_READY_SELECTED = new Color(list.getSelectionBackground().getBlue(), list.getSelectionBackground().getRed()/2,list.getSelectionBackground().getGreen()/2, list.getSelectionBackground().getAlpha());
		}
		return NOT_READY_SELECTED;
	}

	private Color getNotReadyColor(JList list) {
		if (NOT_READY == null) {
			NOT_READY = new Color(list.getSelectionBackground().getBlue(), list.getSelectionBackground().getRed(),list.getSelectionBackground().getGreen(), list.getSelectionBackground().getAlpha());
		}
		return NOT_READY;
	}

}