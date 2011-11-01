package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import stencil.explore.model.sources.TwitterSource;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.util.streams.twitter.TwitterTuples;
import stencil.util.collections.ArrayUtil;

public class Twitter extends SourceEditor {
	private static final long serialVersionUID = 4349967365836435540L;

	protected final JTextField elements = new JTextField();
	private final JTextField keywords = new JTextField();

	public Twitter(TwitterSource source) {
		this();
		set(source);
	}

	private Twitter() {
		super("");
		keywords.setText("");

		String names = ArrayUtil.prettyString(TuplePrototypes.getNames(TwitterTuples.PROTOTYPE));
		elements.setText(names);
		elements.setEditable(false);

		
		this.add(labeledPanel("Header:", elements));
		this.add(labeledPanel("Keywords: ", keywords));
		
		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		keywords.addFocusListener(fl);
	}

	/**Sets the passed file source.
	 * If source is null, the save target will be returned.*/
	public TwitterSource get() {
		return new TwitterSource(name, keywords.getText());
	}

	/**Set the current state to match the source passed.
	 * SourcesChanged will be remember for automatic saving as well.
	 *
	 * SourcesChanged cannot be null.
	 **/
	public void set (TwitterSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		keywords.setText(source.keywords());
	}

}