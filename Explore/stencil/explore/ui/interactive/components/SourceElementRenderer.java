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