package stencil.display;

import java.util.Collection;

import javax.swing.JComponent;

import stencil.adapters.java2D.data.Guide2D;
import stencil.util.Selector;

/**What should a canvas component be able to do?*/
public abstract class DisplayCanvas extends JComponent {

	public abstract DisplayGuide getGuide(Selector sel);
	public abstract void addGuide(Selector sel, Guide2D guide);
	public abstract boolean hasGuide(Selector sel);
	public abstract Collection<Guide2D> getGuides();

}
