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

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import stencil.explore.model.sources.SourceCache;
import stencil.explore.model.sources.StreamSource;

/**Root of the source editors.  Provides default functionality and consistently look,
 * as well as utlities to preserve the consistent look.
 * 
 * Defaults:
 * 		Default layout is Box Layout along Y-axis
 * 
 * @author jcottam
 *
 */
@SuppressWarnings("serial")
public abstract class SourceEditor extends JPanel {
	protected String name;
	
	public SourceEditor(String name) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.name =name;
	}
	
	protected void set(StreamSource source) {this.name = source.name();}
	protected abstract StreamSource get();
	
	protected void saveValues() {
		SourceCache.put(get());
		fireChangeEvent();
	}
	
	protected void fireChangeEvent() {
		ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
		for (int i = 0 ; i < listeners.length; i++) {
			ChangeEvent e =new ChangeEvent(this);
			listeners[i].stateChanged(e);
		}
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	

	/**Create a panel containing the label and the component.  
	 * Will apply layout rules according to the component type.
	 * @param label
	 * @param body
	 * @return
	 */
	protected static JPanel labeledPanel(String label, JComponent body) {return labeledPanel(label, body, null);}
	protected static JPanel labeledPanel(String label, JComponent body, JComponent suffix) {
		JPanel p = new JPanel();
		JLabel l = new JLabel(label);
		
		if (body instanceof JTextField) {p.setLayout(new BorderLayout());}
		
		p.add(l, BorderLayout.WEST);
		p.add(body, BorderLayout.CENTER);
		
		if (suffix != null) {
			p.add(suffix, BorderLayout.EAST);
		}

		return p;
	}


}
