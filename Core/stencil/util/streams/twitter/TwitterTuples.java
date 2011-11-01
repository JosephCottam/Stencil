package stencil.util.streams.twitter;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import twitter4j.*;
import twitter4j.conf.*;

import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.tuple.stream.TupleStream;

public class TwitterTuples implements TupleStream {
	public static final String[] FIELDS = new String[]{"message", "user", "date"};
	public static final TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS);
	
	private final String name;	//Name of the stream
	private final TwitterStream generalStream;
	private final Queue<Status> queue = new ArrayDeque();

	private final StatusListener listener = new StatusListenerImpl(queue);
	
	private static final class StatusListenerImpl implements StatusListener {
		private final Queue<Status> queue;
		public StatusListenerImpl(Queue<Status> queue) {
			this.queue = queue;
		}
		
		public void onException(Exception arg0) {}
		public void onDeletionNotice(StatusDeletionNotice arg0) {}
		public void onScrubGeo(long arg0, long arg1) {}
		public void onTrackLimitationNotice(int arg0) {}
		public void onStatus(Status status) {
			synchronized(queue){queue.add(status);}
		}
	}
	
	protected void finalize() {
		generalStream.cleanUp();
	}
	
	public TwitterTuples(String name, String username, String password) throws TwitterException {
		this.name = name;
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setUser(username)
			   .setPassword(password)
			   .setOAuthConsumerKey("M1COCjERiQlZHEpro5Aoxg")
			   .setOAuthConsumerSecret("XgVmCNShzpfKG1LhDUxwG5Z6nzzhO2xOHkRfqpwsnKc")
			   .setOAuthAccessToken("18570099-nPaF5WO2h0XGRsvgeZSP6VPhXcKNUcSaIQkm4eM")
			   .setOAuthAccessTokenSecret("Y8F3NcialrzXWou4OqAgrdYKvWMCnsS8WRd6INFuLtY");
		Configuration config = builder.build();
		
		generalStream = new TwitterStreamFactory(config).getInstance();
		generalStream.addListener(listener);
		generalStream.sample();
	}
	
	public SourcedTuple next() {		
		Status s;
		synchronized(queue) {s = queue.poll();}
		if (s == null) {return null;}
		
		Object[] vals = new Object[PROTOTYPE.size()];
		vals[0] =s.getText();
		vals[1] =s.getUser().getName();
		vals[2] =s.getCreatedAt();
		
		PrototypedArrayTuple t = new PrototypedArrayTuple(PROTOTYPE, vals);
		return new SourcedTuple.Wrapper(name, t);
	}

	public boolean hasNext() {return true;}

	public void remove() {throw new UnsupportedOperationException();}
	public List<String> getFields() {return Arrays.asList(TuplePrototypes.getNames(PROTOTYPE));}
}
