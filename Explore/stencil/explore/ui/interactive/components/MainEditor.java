package stencil.explore.ui.interactive.components;

import static stencil.parser.string.StencilParser.LIST_STREAM_DECLS;

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
import stencil.parser.ParseStencil;
import stencil.parser.tree.StencilTree;


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
		StencilTree program;
		
		try {program = ParseStencil.checkParse(stencilEditor.getStencil());}
		catch (Exception e) {return;}

		//Synch Sources
		Set<StreamSource> sources = new TreeSet<StreamSource>();
		try {
			for (StencilTree stream: program.find(LIST_STREAM_DECLS)) {
				StreamSource source;
				if (SourceCache.weakContains(stream.getText())) {
					source = SourceCache.weakGet(stream.getText());
				} else {
					if (stream.getText().equals(MouseSource.NAME)) { //If it was named after the mouse, assume it is the mouse
						source = new MouseSource(stream.getText());
					} else {
						source = new FileSource(stream.getText()); 	//If we've had nothing of this name before, assume its a file
					}
				}
				sources.add(source);
			}
		} catch (Exception e) {/*Ignore exceptions in this process*/}
		
		sourcesEditor.setSources(sources);
	}
}
