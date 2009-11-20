package stencil.explore.model.sources;

 import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.explore.ui.components.sources.Twitter;
import stencil.tuple.TupleStream;
import stencil.util.streams.feed.TwitterTuples;



public final class TwitterSource extends StreamSource {
	public static final String NAME = "Twitter";
	
	private final String feedURL;
	private final String username;
	private final String password;
	
	public TwitterSource(String name) {this(name, "", "", "");}
	public TwitterSource(String name, String feedURL, String username, String password) {
		super(name);
		this.feedURL = feedURL == null ? feedURL : feedURL.trim();
		this.username = username == null ? username : username.trim();
		this.password = password == null ? password : password.trim();
	}

	public SourceEditor getEditor() {return new Twitter(this);}

	public String header() {return TwitterTuples.HEADER;}
	public String feedURL() {return feedURL;}
	public String username() {return username;}
	public String password() {return password;}
	
	public TwitterSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new TwitterSource(name, feedURL, username, password);
	}
	
	public TwitterSource password(String password) {
		if (this.password.equals(password)) {return this;}
		return new TwitterSource(name, feedURL, username, password);
	}
	
	public TwitterSource username(String username) {
		if (this.username.equals(username)) {return this;}
		return new TwitterSource(name, feedURL, username, password);
	}
	
	public TwitterSource feedURL(String feedURL) {
		if (this.feedURL.equals(feedURL)) {return this;}
		return new TwitterSource(name, feedURL, username, password);
	}
	
	public TupleStream getStream(Model context) throws Exception {
		if (username !=null && !username.equals("")) {return new TwitterTuples(name, feedURL, username, password);}
		return new TwitterTuples(name, feedURL);
	}

	public boolean isReady() {
		if (feedURL == null || feedURL.equals("")) {return false;}
		return true;
	}

	public TwitterSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		TwitterSource result = this;
		
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("FEED_URL")) {
				String feedURL = line.substring(line.indexOf(":") +2);
				result = result.feedURL(feedURL);
			} else if (line.startsWith("USERNAME")) {
				String username = line.substring(line.indexOf(":")+2);
				result = result.username(username);
			} else if (line.startsWith("PASSWORD")) {
				String password = line.substring(line.indexOf(":") +2);
				result = result.password(password);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("FEED_URL: ");
		b.append(feedURL);
		b.append("\n");
		b.append("USERNAME: ");
		b.append(username);
		b.append("\n");
		b.append("PASSWORD: ");
		b.append(password);
		b.append("\n");
		return b.toString();
	}

}
