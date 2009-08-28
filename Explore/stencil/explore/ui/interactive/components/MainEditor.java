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
import java.util.TreeSet;
import java.util.Set;
import javax.swing.*;

import stencil.explore.ui.components.events.TextPositionChangedListener;
import stencil.explore.ui.components.*;
import stencil.explore.coordination.StencilListener;
import stencil.explore.coordination.StencilEvent;
import stencil.explore.model.sources.FileSource;
import stencil.explore.model.sources.MouseSource;
import stencil.explore.model.sources.SourceCache;
import stencil.explore.model.sources.StreamSource;
import stencil.parser.tree.Program;
import stencil.parser.tree.External;
import stencil.parser.string.ParseStencil;


public class MainEditor extends JPanel implements StencilListener.StencilChanged, TextPositionChangedListener {
	protected StencilEditorPanel stencilEditor;
	protected SourceListEditor sourcesEditor;
	protected StatusBar statusBar;

	protected JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


	public MainEditor() {
		this.stencilEditor = new StencilEditorPanel();
		this.sourcesEditor = new SourceListEditor();

		split.setLeftComponent(stencilEditor);
		split.setRightComponent(sourcesEditor);

		split.setResizeWeight(1);//All extra goes to the left region
		naturalSize();

		statusBar = new StatusBar();

		stencilEditor.addStencilChangedListener(this);
		stencilEditor.addTextPositionChangedListener(this);
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);

	}

	public StencilEditorPanel getStencilEditor() {return stencilEditor;}
	public SourceListEditor getSourcesEditor() {return sourcesEditor;}


	public void textPositionChanged(int line, int charNum) {
		statusBar.setLineNum(line+1);
		statusBar.setCharNum(charNum+1);
	}

	public void naturalSize() {split.setDividerLocation(.75);}

	public JMenu getEditMenu() {
		return stencilEditor.getEditMenu();
	}


	public void stencilChanged(StencilEvent.StencilChanged stencilUpdate) {
		Program program;
		
		try {program = ParseStencil.checkParse(stencilEditor.getStencil());}
		catch (Exception e) {return;}

		//Synch Sources
		Set<StreamSource> sources = new TreeSet<StreamSource>();
		try {
			for (External stream: program.getExternals()) {
				StreamSource source;
				if (SourceCache.weakContains(stream.getName())) {
					source = SourceCache.weakGet(stream.getName());
				} else {
					if (stream.getName().equals(MouseSource.NAME)) { //If it was named after the mouse, assume it is the mouse
						source = new MouseSource(stream.getName());
					} else {
						source = new FileSource(stream.getName()); 	//If we've had nothing of this name before, assume its a file
					}
				}
				sources.add(source);
			}
		} catch (Exception e) {/*Ignore exceptions in this process*/}

		sourcesEditor.setSources(sources);
	}
}
