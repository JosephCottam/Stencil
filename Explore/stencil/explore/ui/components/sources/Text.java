package stencil.explore.ui.components.sources;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import stencil.explore.ui.interactive.Interactive;
import stencil.explore.model.sources.TextSource;

public final class Text extends stencil.explore.ui.components.sources.SourceEditor {
	private static final long serialVersionUID = -6217403692229831654L;

	private final JTextArea data;
	private final JTextField tupleSize;
	private final JTextField separator;

	public Text(TextSource source) {
		this();
		set(source);
	}

	private Text() {
		super("");
		tupleSize = new JTextField();
		separator = new JTextField();
		data = new JTextArea();

		setLayout(new BorderLayout());

		JPanel config = new JPanel();

		JPanel headerPanel = new JPanel();
		JLabel headerLabel = new JLabel("Header:");
		headerPanel.setLayout(new BorderLayout());
		headerPanel.add(headerLabel, BorderLayout.WEST);
		headerPanel.add(tupleSize, BorderLayout.CENTER);


		JPanel separatorPanel = new JPanel();
		JLabel seperatorLabel = new JLabel("Separator");
		separatorPanel.setLayout(new BorderLayout());
		separatorPanel.add(seperatorLabel, BorderLayout.WEST);
		separatorPanel.add(separator, BorderLayout.CENTER);

		Dimension size = new Dimension(separator.getPreferredSize().height, 20);
		separator.setPreferredSize(size);
		separator.setMinimumSize(size);
		separator.setMinimumSize(size);
		separator.setSize(size);

		config.setLayout(new BorderLayout());
		config.add(headerPanel, BorderLayout.CENTER);
		config.add(separatorPanel, BorderLayout.EAST);

		JScrollPane scroller = new JScrollPane(data, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		data.setFont(Interactive.styleFont(Interactive.PROGRAM_FONT_NAME));
		data.setTabSize(3);

		add(config, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);


		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		data.addFocusListener(fl);
		separator.addFocusListener(fl);
		tupleSize.addFocusListener(fl);
		this.addFocusListener(fl);
	}

	public void set(TextSource source) {
		assert source != null : "Cannot pass a null to 1-argument set.";
		super.set(source);
		tupleSize.setText(Integer.toString(source.size()));
		separator.setText(source.separator());
		data.setText(source.text());
	}

	public TextSource get() {
		return new TextSource(name, Integer.parseInt(tupleSize.getText()), separator.getText(), data.getText(), delay.isSelected());
	}

}
