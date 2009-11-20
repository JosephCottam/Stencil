package stencil.util.streams.feed;

import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.util.collections.ConditionSet;

public abstract class CacheFeed<T> implements TupleStream {
	public static int SLEEP_DELAY = 500;

	protected URL url;
	protected String name;
	protected T feed;
	protected Queue<Tuple> entryCache = new LinkedList();
	protected ConditionSet idCache;
	protected Thread updater;

	public CacheFeed(String name, String url, ConditionSet cache, T feed) throws Exception {
		this(name, new URL(url), cache, feed);
	}

	public CacheFeed(String name, URL url, ConditionSet cache, T feed) throws Exception {
		this.name = name;
		this.url = url;
		this.idCache = cache;
		this.feed = feed;
	}

	/**Extend this method to work with feed-type-specific cache updates.
	 * Next will use this method to update the entry cache periodically.
	 * */
	protected abstract void updateEntryCache();

	/**Updates the entry cache in a manner that is synchronized against the 'next' method.*/
	protected synchronized void synchUpdateEntryCache() {updateEntryCache();}

	public void close() throws Exception {
		entryCache.clear();
		feed=null;
	}

	public synchronized Tuple next() {
		if (feed == null) {throw new NoSuchElementException("Feed stream not ready.");}

		if (entryCache.isEmpty()) {updateEntryCache();}
		while (entryCache.isEmpty()) {
			try {Thread.sleep(SLEEP_DELAY);}
			catch(Exception e) {throw new Error("Error sleeping while waiting for feed update.",e);}
			updateEntryCache();
		}

		return entryCache.remove();
	}

	/**Feeds are assumed to be infinite, so this method always returns
	 * if the connection has not been closed.
	 *
	 * This only means that 'next()' will not immediately throw an exception,
	 * but it may generate a time-out exception after a given period.
	 */
	public boolean hasNext() {return feed != null;}
	public boolean ready() {
		if (entryCache.isEmpty() && updater == null) {
			updater = new Thread() {public void run() {synchUpdateEntryCache(); updater = null;}};
			updater.start();
		}
		return entryCache.isEmpty();
	}

	public void remove() {throw new UnsupportedOperationException();}


	/**Clears caches.*/
	public void reset() throws Exception {
		entryCache.clear();
		idCache.clear();
	}
}
