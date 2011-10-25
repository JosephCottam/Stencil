package stencil.explore.util;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class ResizeLayout extends BoxLayout {
	public ResizeLayout(Container arg0, int arg1) {super(arg0, arg1);}
	
	@Override
	public void layoutContainer(Container parent) {
		super.layoutContainer(parent);
		Dimension d = parent.getPreferredSize();
		d.setSize(d.getWidth(), d.getHeight() + 20);
		((JFrame) parent.getParent().getParent().getParent()).setSize(d);
	}

}
