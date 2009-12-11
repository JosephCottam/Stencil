package stencil.util.streams.feed;

import java.util.*;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.util.collections.MarkSweepSet;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.net.URL;

public class FeedTuples extends CacheFeed<SyndFeedInput> {
	protected final String keyField;
	protected final List<String> fields;
	protected final List<Class> types;
	public FeedTuples(String name, String url, String keyField, String[] fields) throws Exception {this(name, new URL(url), keyField, fields);}

	public FeedTuples(String name, URL url, String keyField, String[] fields) throws Exception {
		super(name, url, new MarkSweepSet(), new SyndFeedInput());
		this.keyField = keyField;
		this.fields = Arrays.asList(fields);
		this.types = Tuples.defaultTypes(fields.length);
	}

	protected void updateEntryCache() {
		List<SyndEntry> entries;
		try {
			entries = feed.build(new XmlReader(url)).getEntries();
		} catch (Exception e) {
			throw new RuntimeException("Error updating feed.", e);
		}

        Collections.sort(entries, new Comparator<SyndEntry> () {
	    	public int compare(SyndEntry o1, SyndEntry o2) {
	        	return o1.getPublishedDate().compareTo(o2.getPublishedDate());
	        }
	    });

        idCache.sweep();

        for(SyndEntry entry: entries) {
        	Map<String, String> fieldValues = mapFields(entry);

        	String key = fieldValues.get(keyField);
        	if (idCache.contains(key)) {continue;}

        	String[] values = new String[fields.size()];
        	for(int i=0; i< fields.size(); i++) {
        		values[i] = fieldValues.get(fields.get(i));
        	}

        	Tuple tuple = new PrototypedTuple(name, fields, types, Arrays.asList(values));
            entryCache.offer(tuple);
            idCache.add(key);
        }
	}

	private Map<String, String> mapFields(SyndEntry entry) {
		Map<String, String> map = new HashMap();
		//HACK: This is unsafe, the API specifically makes the getForeignMarkup 'opaque'
		for (int i =0; i< ((java.util.ArrayList) entry.getForeignMarkup()).size(); i++) {
			org.jdom.Element e = (org.jdom.Element) ((java.util.ArrayList) entry.getForeignMarkup()).get(i);
			map.put(e.getName(), e.getValue());
		}

		map.put("TITLE", entry.getTitle());
		map.put("AUTHOR", entry.getAuthor());
		map.put("LINK", entry.getLink());
		map.put("DESC", entry.getDescription().toString());

		return map;
	}
}
