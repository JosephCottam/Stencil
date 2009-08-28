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
package stencil.explore.ui.applet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import static stencil.explore.Application.reporter;
import stencil.explore.coordination.*;
import stencil.explore.coordination.StencilEvent.Type;
import stencil.explore.model.Model;
import stencil.explore.model.sources.SourceCache;
import stencil.explore.model.sources.TextSource;
import stencil.explore.ui.components.*;
import stencil.parser.tree.Program;
import stencil.parser.tree.External;
import stencil.parser.string.ParseStencil;
import stencil.explore.Application;
import stencil.explore.PropertyManager;

public final class Applet extends JApplet implements StencilListener.StencilChanged  {
	private static final String CUSTOM_STENCIL = "[Custom]";
	private static final String APPLET_CONFIG = "Applet.properties";
	private static final String STENCIL_PREFIX ="stencil";

	protected boolean internalUpdate = false;

	protected JTabbedPane tabs;

	protected Model model;
	protected Controller controller = new Controller();

	protected MessagePanel messages = new MessagePanel();
	protected StencilEditorPanel editor = new StencilEditorPanel();
	protected JPanel stencilContentPanel = new JPanel(new BorderLayout());
	protected SourcesPanel sources = new SourcesPanel();
	protected JComboBox stencils = new JComboBox();

	public void init() {
		Application.reporter = messages;
		
		loadProperties();
		tabs = new JTabbedPane();

		editor.addStencilChangedListener(controller);
		sources.addStencilChangedListener(controller);

		controller.addMutable(editor, Type.Stencil);
		controller.addMutable(sources, Type.Sources);

		setModel(new Model());

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(editor, BorderLayout.CENTER);

		JPanel controls = new JPanel();
		JButton executeOnMain = new JButton("Execute");
		controls.add(executeOnMain);
		controls.add(stencils);
		main.add(controls, BorderLayout.SOUTH);

		JPanel sourcesPanel = new JPanel();
		sourcesPanel.setLayout(new BorderLayout());
		sourcesPanel.add(sources, BorderLayout.CENTER);

		controls = new JPanel();
		JButton executeOnSources = new JButton("Execute");
		controls.add(executeOnSources);
		sourcesPanel.add(controls, BorderLayout.SOUTH);

		tabs.addTab("Editor", main);
		tabs.addTab("Sources", sourcesPanel);
		tabs.addTab("Stencil", stencilContentPanel);
		tabs.addTab("Messages", messages);

		editor.addStencilChangedListener(this);

		ActionListener executeListener = new ActionListener() {public void actionPerformed(ActionEvent e) {
				if (model.isRunning()) {System.err.println("Stencil already executing."); return;}

				reporter.clear();
				try {model.compile();}
				catch (Exception ex) {
					stencilContentPanel.removeAll();
					reporter.addError("Error in compile.  Execution not attempted.");
					throw new RuntimeException("Error compiling stencil.", ex);
				}

				try {
					//Select the stencil tab
					tabs.setSelectedIndex(1);

					//Reset the content pane
					stencilContentPanel.removeAll();
					stencilContentPanel.add(model.getStencilPanel(), BorderLayout.CENTER);
					stencilContentPanel.revalidate();  //Forces the layout to execute NOW

					//Run it!
					model.execute();
				}
				catch (Exception ex) {
					reporter.addError("Execution aborted.");
					reporter.addError(ex.getMessage());
					ex.printStackTrace();
				}
			}
		};

		executeOnMain.addActionListener(executeListener);
		executeOnSources.addActionListener(executeListener);

		stencils.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (internalUpdate) {return;}
				StencilItem item = (StencilItem) stencils.getSelectedItem();
				editor.setStencil(item.stencil);
			}
		});

		this.add(tabs);
	}

	private void loadProperties() {
		Properties props = PropertyManager.loadProperties(new String[]{APPLET_CONFIG});
		DefaultComboBoxModel model = (DefaultComboBoxModel) stencils.getModel();

		model.addElement(new StencilItem(CUSTOM_STENCIL, ""));

		//Locate and clean-up stencils
		for (Object n: props.keySet()) {
			String name =(String) n;
			if (name.startsWith(STENCIL_PREFIX)) {
				String stencil = props.getProperty(name);
				name = name.substring(STENCIL_PREFIX.length() +1);

				StencilItem item = new StencilItem(name, stencil);
				model.addElement(item);
			}
		}

	}

	/**Synch the interactive application to the state of the passed application.*/
	public void setModel(Model model) {
		controller.removeMutable(model, Type.All);

		this.model = model;
		controller.addMutable(model, Type.All);
		model.addAllListeners(controller);
		model.fireAll();

		stencilContentPanel.removeAll();
		if (model.getStencilPanel() != null) {
			stencilContentPanel.add(model.getStencilPanel(), BorderLayout.CENTER);
		}
	}

	public void stencilChanged(StencilEvent.StencilChanged stencilUpdate) {
		Program program;
		try {program = ParseStencil.parse(editor.getStencil(), model.getAdapterOpts().getAdapter());}
		catch (Exception e) {return;}

		//Synch Sources
		Set<TextSource> sources = new HashSet<TextSource>();
		try {
			for (External stream: program.getExternals()) {
				TextSource source;
				if (SourceCache.weakContains(stream.getName())) {
					source = (TextSource) SourceCache.weakGet(stream.getName());
				} else {
					source = new TextSource(stream.getName());
				}
				sources.add(source);
			}
		} catch (Exception e) {/*Ignore exceptions in this process*/}


		model.setSources(sources);
		this.sources.setSources(sources);

		if (!((StencilItem)stencils.getSelectedItem()).stencil.equals(editor.getStencil())) {
			//Save old stencil (will not retain data...sorry)
			//TODO: Modify StencilItem so it is really a StencilEditor (and thus could retain data)
			StencilItem save = new StencilItem(CUSTOM_STENCIL, editor.getStencil());
			((DefaultComboBoxModel) stencils.getModel()).removeElementAt(0);
			((DefaultComboBoxModel) stencils.getModel()).addElement(save);
			internalUpdate = true;
			stencils.setSelectedIndex(0);
			internalUpdate = false;
		}
	}


}
