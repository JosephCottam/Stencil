package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import stencil.explore.model.sources.TwitterSource;
import javax.swing.*;

public final class Twitter extends SourceEditor {
	private static final long serialVersionUID = 256130086126675495L;

	private TwitterSource saveTarget;
	private final JTextField feedURL = new JTextField();
	private final JTextField username= new JTextField();
	private final JTextField password= new JTextField();	
	
	public Twitter(TwitterSource source) {
		this();
		set(source);
	}
	
	private Twitter() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(labeledPanel("Feed URL: ", feedURL));
		this.add(labeledPanel("Username: ", username));
		this.add(labeledPanel("Password: ", password));
		

		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		feedURL.addFocusListener(fl);
		username.addFocusListener(fl);
		password.addFocusListener(fl);
	}

	/**Perform an auto-save.*/
	public void saveValues() {
		if (saveTarget != null) {get(saveTarget);}
		this.fireChangeEvent();
	}
	
	public TwitterSource get(TwitterSource source) {
		if (source == null) {source = saveTarget;}
		
		source.setFeedURL(feedURL.getText());
		source.setUsername(username.getText());
		source.setPassword(password.getText());
		return source;
	}
	
	public void set(TwitterSource source) {
		assert source != null : "Cannot pass a null to set.";

		saveTarget = source;

		feedURL.setText(source.getFeedURL());
		username.setText(source.getUsername());
		password.setText(source.getPassword());
	}
}
