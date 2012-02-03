package stencil.explore.ui.interactive;

import java.awt.BorderLayout;
import javax.swing.*;

import stencil.explore.ui.components.events.TextPositionChangedListener;
import stencil.explore.ui.components.*;
import stencil.explore.coordination.StencilListener;
import stencil.explore.coordination.StencilEvent;
import stencil.parser.ParseStencil;


public class MainEditor extends JPanel implements StencilListener.StencilChanged, TextPositionChangedListener {
	protected StencilEditorPanel stencilEditor;
	protected StatusBar statusBar;

	protected JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


	public MainEditor() {
		this.stencilEditor = new StencilEditorPanel();

		split.setLeftComponent(stencilEditor);

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


	public void textPositionChanged(int line, int charNum) {
		statusBar.setLineNum(line+1);
		statusBar.setCharNum(charNum+1);
	}

	public void naturalSize() {split.setDividerLocation(.75);}

	public JMenu getEditMenu() {
		return stencilEditor.getEditMenu();
	}


	public void stencilChanged(StencilEvent.StencilChanged stencilUpdate) {
		try {ParseStencil.checkParse(stencilEditor.getStencil());}
		catch (Exception e) {return;}
	}
}