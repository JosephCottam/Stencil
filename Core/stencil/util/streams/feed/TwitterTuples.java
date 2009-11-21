package stencil.util.streams.feed;

import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import twitter4j.http.HttpClient;
import twitter4j.http.Response;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;

import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.util.collections.*;

//Based on FeedMonitor (http://yusuke.homeip.net/twitter4j/en/javadoc/twitter4j/examples/FeedMonitor.html)
public class TwitterTuples extends CacheFeed<HttpClient> {
	public static final String HEADER = "MESSAGE, LINK, SOURCE, TIME";
	protected static final String[] FIELDS = HEADER.split(", ");
	
	private static class TwitterTuple implements Tuple {
		private String[] values = new String[FIELDS.length];

		public TwitterTuple(String feed, String message, String link, String time) {
			values[0] = message;
			values[1] = link;
			values[2] = feed;
			values[3] = time;
		}

		public Object get(String name) throws InvalidNameException {
			for (int i=0; i< FIELDS.length; i++) {
				if (name.toUpperCase().equals(FIELDS[i])) {return values[i];}
			}
			throw new InvalidNameException(name);
		}

		public Object get(int idx) {
			try {return values[idx];}
			catch (IndexOutOfBoundsException e) {throw new TupleBoundsException(idx, size());}
		}
		
		public List<String> getPrototype() {return java.util.Arrays.asList(FIELDS);}
		public int size() {return FIELDS.length;}
		
		public boolean isDefault(String name, Object value) {return "".equals(value);}
	}

	public TwitterTuples(String name, String feedURL) throws Exception {
		this(name, feedURL,new HttpClient());
	}

	public TwitterTuples(String name, String feedURL, String twitterID, String password) throws Exception {
		this(name, feedURL, new HttpClient(twitterID, password));
	}

	private TwitterTuples(String name, String feedURL, HttpClient feed) throws Exception {
		super(name, feedURL, new TimeSet(60000), feed);
	}

	protected void updateEntryCache() {
		List<SyndEntry> entries;

		try {
			Response res = feed.get(url.toString());
			entries = new SyndFeedInput().build(res.asDocument()).getEntries();
		} catch (Exception e) {throw new RuntimeException("Exception retrieving twitter status.", e);}

        Collections.sort(entries, new Comparator<SyndEntry> () {
	    	public int compare(SyndEntry o1, SyndEntry o2) {
	        	return o1.getPublishedDate().compareTo(o2.getPublishedDate());
	        }
	    });

        idCache.sweep();
        for (SyndEntry entry : entries) {
            String link = entry.getLink();

        	if (idCache.contains(link)) {continue;}


        	Date published = entry.getPublishedDate();
            String message = entry.getTitle();

            entryCache.offer(new TwitterTuple(name, message, link, published.toString()));
            idCache.add(link);
        }
	}
}