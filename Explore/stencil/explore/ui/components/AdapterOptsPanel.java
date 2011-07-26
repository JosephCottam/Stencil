package stencil.explore.ui.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import stencil.explore.coordination.*;
import stencil.explore.model.AdapterOpts;

public final class AdapterOptsPanel extends JPanel implements StencilMutable.Config {
	private static final String DEFAULT_COLOR = "None";
	protected static final List<String> COLORS = new ArrayList<String>();

	static {
		COLORS.add("None");
		COLORS.add("Red");
		COLORS.add("Blue");
		COLORS.add("Green");
		COLORS.add("Black");
		COLORS.add("Orange");
	}

	private final JComboBox debug = new JComboBox();
	private final JCheckBox defaultMouse = new JCheckBox();
	private final JComboBox adapter = new JComboBox();
	
	
	public AdapterOptsPanel() {
		defaultMouse.setText("Default Mouse Handling");

		JPanel debugPanel = new JPanel();
		JLabel debugLabel = new JLabel("Debug Color:");
		debugPanel.add(debugLabel);
		debugPanel.add(this.debug);

		JPanel adapterPanel = new JPanel();
		JLabel adapterLabel = new JLabel("Adapter:");
		adapterPanel.add(adapterLabel);
		adapterPanel.add(this.adapter);

		this.add(defaultMouse);
		this.add(adapterPanel);
		this.add(debugPanel);

		setLayout(new GridLayout());

		//Populate lists
		for (String c: COLORS) {debug.addItem(c);}
		for (String a: AdapterOpts.adapterMap.keySet()) {adapter.addItem(a);}

		//Listeners for change events.  Indicate something MIGHT have changed.
		defaultMouse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {fireOptsChanged();}
		});

		adapter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {fireOptsChanged();}
		});

		debug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {fireOptsChanged();}
		});

	}

	/**Given the compielrOpts passed, set all controls accordingly.*/
	public void setAdapterOpts(AdapterOpts source) {
		if (source.getDebug() == null) {debug.setSelectedItem("null");}
		else {debug.setSelectedItem(source.getDebug().toString());}

		adapter.setSelectedItem(source.getAdapterName());
		defaultMouse.setSelected(source.getDefaultMouse());
	}

	/**Given the current settings, what is a AdapterOpts that captures them?*/
	public AdapterOpts get() {
		Color debugMarker = null;
		if (this.debug.getSelectedIndex() >0) {
			try {debugMarker = (Color) Color.class.getField(this.debug.getSelectedItem().toString().toUpperCase()).get(null);}
			catch (Exception e) {throw new Error("Error in name/color matching.");}
		}

		return new AdapterOpts((String) adapter.getSelectedItem(), debugMarker, defaultMouse.isSelected(), AdapterOpts.defaultRenderQuality);
	}

	public void addStencilChangedListener(StencilListener.ConfigChanged l) {listenerList.add(StencilListener.ConfigChanged.class, l);}

	public void fireOptsChanged() {
		StencilListener.ConfigChanged[] listeners = listenerList.getListeners(StencilListener.ConfigChanged.class);
		for (int i = 0 ; i < listeners.length; i++) {
			StencilEvent.ConfigChanged u = new StencilEvent.ConfigChanged(this, get());
			listeners[i].configChanged(u);
		}
	}

	/**Set the list that will be used to populate the debugging color options.
	 * This setting will only take effect if it is done before the AdapterOptsPanel is instantiated.
	 * The DEFAULT_COLOR value will always be the first item in the list, regardless of values
	 * included in the list passed.
	 *
	 * Only names found in the static variables of java.awt.Color can be used.
	 */
	public static void setDebugColors(List<String> colors) {
		COLORS.clear();
		COLORS.add(DEFAULT_COLOR);
		for (String c: colors) {COLORS.add(c);}
	}
}