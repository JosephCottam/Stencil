package stencil.explore.model.sources;

 import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.explore.ui.components.sources.Twitter;
import stencil.streams.TupleStream;
import stencil.util.streams.feed.TwitterTuples;



public final class TwitterSource extends StreamSource {
	public static final String NAME = "Twitter";
	
	private String feedURL;
	private String username;
	private String password;
	
	public TwitterSource(String name) {super(name);}

	public SourceEditor getEditor() {return new Twitter(this);}

	public String getHeader() {return TwitterTuples.HEADER;}
	public String getFeedURL() {return feedURL;}
	public String getUsername() {return username;}
	public String getPassword() {return password;}
	
	public void setPassword(String password) {this.password = password;}
	public void setUsername(String username) {this.username = username==null?username:username.trim();}
	public void setFeedURL(String feedURL) {this.feedURL = feedURL == null?feedURL:feedURL.trim();}
	
	public TupleStream getStream(Model context) throws Exception {
		if (username !=null && !username.equals("")) {return new TwitterTuples(name, feedURL, username, password);}
		return new TwitterTuples(name, feedURL);
	}

	public boolean isReady() {
		if (feedURL == null || feedURL.equals("")) {return false;}
		return true;
	}

	public void restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				name = line.substring(line.indexOf(":") +2);
			} else if (line.startsWith("FEED_URL")) {
				feedURL = line.substring(line.indexOf(":") +2);
			} else if (line.startsWith("USERNAME")) {
				username = line.substring(line.indexOf(":")+2);
			} else if (line.startsWith("PASSWORD")) {
				password = line.substring(line.indexOf(":") +2);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
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
