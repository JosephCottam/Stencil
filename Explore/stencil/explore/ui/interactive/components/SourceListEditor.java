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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import stencil.explore.coordination.StencilEvent;
import stencil.explore.coordination.StencilListener;
import stencil.explore.coordination.StencilMutable;
import stencil.explore.model.sources.*;
import stencil.explore.ui.components.sources.*;
import stencil.explore.util.ListModel;
import stencil.explore.util.StencilIO;;

public class SourceListEditor extends JPanel implements ChangeListener, StencilMutable.Sources<StreamSource> {
	protected JList streamSources = new JList();
	protected JComboBox sourceType = new JComboBox();
	protected JPanel streamChangeEditor;
	protected SourceEditor streamEditor;

	protected ListModel<StreamSource> listModel = new ListModel();

	/**Flag to indicate internal data changes are being made.
	 * It is used to suspend certain event notifications while the updates are being made.
	 */
	protected boolean internalUpdate = false;

	public SourceListEditor() {
		clearStreamList();
		streamSources.setCellRenderer(new SourceElementRenderer());

		setLayout(new BorderLayout());

		JScrollPane streamsScroller = new JScrollPane(streamSources, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(streamsScroller, BorderLayout.CENTER);

		sourceType = new JComboBox();
		sourceType.addItem(FileSource.NAME);
		sourceType.addItem(DBSource.NAME);
		sourceType.addItem(MouseSource.NAME);
		sourceType.addItem(TwitterSource.NAME);
		sourceType.addItem(WindowStateSource.NAME);
		sourceType.addItem(RandomSource.NAME);
		sourceType.addItem(SequenceSource.NAME);

		streamChangeEditor = new JPanel();
		streamChangeEditor.setLayout(new BorderLayout());
		streamChangeEditor.add(sourceType, BorderLayout.NORTH);
		streamChangeEditor.setVisible(false);
		this.add(streamChangeEditor, BorderLayout.SOUTH);


		sourceType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (internalUpdate) {return;}

				String type  = (String) sourceType.getSelectedItem();
				String name = ((StreamSource) streamSources.getSelectedValue()).name();
				int idx = streamSources.getSelectedIndex();
				StreamSource source = StencilIO.getByType(name, type);

				if (SourceCache.contains(source)) {source = SourceCache.get(source);}

				listModel.setElementAt(source, idx);
				fireSourcesChangedEvent();
				changeEditorPanel(source.getEditor(), type);
			}
		});

		streamSources.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (streamSources.getSelectedIndex() >= 0) {
					if (streamChangeEditor != null) {
						streamChangeEditor.setVisible(false);
					}

					
					StreamSource source = ((StreamSource) streamSources.getSelectedValue());
					if (SourceCache.contains(source)) {source = SourceCache.get(source);}
					
					SourceEditor editor = source.getEditor();
					changeEditorPanel(editor, getTypeName(source));
				} else {
					streamChangeEditor.setVisible(false);
				}

			}
		});
	}

	protected void changeEditorPanel(SourceEditor panel, String type) {
		internalUpdate = true;

		if (streamEditor != null) {
			streamChangeEditor.remove(streamEditor);
			streamEditor.removeChangeListener(this);
		}

		streamChangeEditor.add(panel, BorderLayout.CENTER);
		streamChangeEditor.setVisible(true);
		streamChangeEditor.revalidate();
		streamEditor = panel;
		streamEditor.addChangeListener(this);
		sourceType.setSelectedItem(type);

		internalUpdate = false;
	}

	/**Given a source, what is the type name?*/
	public static String getTypeName(StreamSource source) {
		try {return (String) source.getClass().getField("NAME").get(null);}
		catch (Exception e) {throw new Error("Could not find static NAME field on source class "  + source.getClass());}
	}

	/**Remove all items from the stream list.*/
	public void clearStreamList() {
		streamSources.clearSelection();
		listModel = new ListModel();
		streamSources.setModel(listModel);
		fireSourcesChangedEvent();
	}

	/**Get a java.util.List of the JList sources.*/
	public List<StreamSource> getList() {return listModel;}

	/**Set the current JList contents from a collection.
	 * List elements will be in the iteration of the collection.*/
	public void setSources(Collection<? extends StreamSource> l) {
		if (listModel.equals(l)) {return;}
		if (l instanceof ListModel) {
			streamSources.setModel((ListModel) l);
			streamSources.clearSelection();
			listModel = (ListModel) l;
			fireSourcesChangedEvent();
		} else {
			clearStreamList();
			for (StreamSource source: l) {
				//If all is set, keep it in the cache.
				if (source.isReady()) {SourceCache.put(source);}
				//If we have something that is all set, get it out
				else if (SourceCache.contains(source)) {source  = SourceCache.get(source);}
				listModel.addElement(source);
			}
			fireSourcesChangedEvent();
		}
	}

	public void stateChanged(ChangeEvent e) {
		for (int i=0; i< listModel.size(); i++) {
			StreamSource s = listModel.get(i);
			StreamSource s2 = SourceCache.get(s);
			listModel.remove(s);
			listModel.add(i, s2);
		}
		
		streamSources.repaint();
		fireSourcesChangedEvent();
	}

	public void addStencilChangedListener(StencilListener.SourcesChanged l) {listenerList.add(StencilListener.SourcesChanged.class, l);}

	public void fireSourcesChangedEvent() {
		StencilListener.SourcesChanged[] listeners = listenerList.getListeners(StencilListener.SourcesChanged.class);
		StencilEvent.SourcesChanged update = new StencilEvent.SourcesChanged(this, listModel);
		for (int i = 0 ; i < listeners.length; i++) {
			listeners[i].sourceChanged(update);
		}
	}
}