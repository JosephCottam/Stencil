package stencil.explore.util;

import javax.swing.JPanel;

/**Indicates that an operator or stream needs to present a panel for interaction during execution.
 * The panel will be placed in a window whose lifetime is controlled by the Explore application.
 * Guaranteed to be called only once per operator instance.
 * @author jcottam
 *
 */
public interface NeedsPanel {
	public JPanel panel();
}
