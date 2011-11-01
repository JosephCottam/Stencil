package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Twitter;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.util.streams.twitter.TwitterTuples;

public class TwitterSource extends StreamSource {
	public static final String NAME = "Twitter";
	private final String keywords;
	
	public TwitterSource(String name) {this(name, "");}
	
	public TwitterSource(String name, String keywords) {
		super(name, TwitterTuples.PROTOTYPE.size());
		this.keywords= keywords;
	}

	
	@Override
	public SourceEditor getEditor() {return new Twitter(this);}

	@Override
	public TwitterTuples getStream(Model context) throws Exception {
		return new TwitterTuples(name, keywords);
	}

	public String header() {return "VALUE";}

	@Override
	public boolean isReady() {return true;}

	@Override
	public TwitterSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new TwitterSource(name, keywords);
	}

	public String keywords() {return keywords;}
	public TwitterSource keywords(String keywords) {
		if (this.keywords.equals(keywords)) {return this;}
		return new TwitterSource(name, keywords);
	}

	@Override
	public TwitterSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		TwitterSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("KEYWORDS")) {
				String keywords = line.substring(line.indexOf(":") +2);
				result = result.keywords(keywords);
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
		b.append("KEYWORDS: ");
		b.append(keywords);
		b.append("\n");
		return b.toString();

	}

}
