package stencil.util.streams.twitter;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import twitter4j.*;
import twitter4j.conf.*;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;

@Description("Retrieve information from twitter.")
@Stream(name="Twitter", spec="[topic:\"\"]")
public class TwitterTuples implements TupleStream {
	public static final String[] FIELDS = new String[]{"message", "user", "tags", "geo", "place", "date"};
	public static final TuplePrototype PROTOTYPE = new TuplePrototype(FIELDS);
	public static final String TOPIC_KEY = "topic";
	
	private final String name;	//Name of the stream
	private final TwitterStream twitter;
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
	
	protected void finalize() {stop();}

	public void stop() {twitter.cleanUp();}
	
	public TwitterTuples(String name, TuplePrototype proto, Specializer spec) throws TwitterException {
		this(name, Converter.toString(spec.get(TOPIC_KEY)));
		if (!proto.nameEqual(PROTOTYPE)) {throw new IllegalArgumentException("Tuple prototype for twitter stream must be: (" + TuplePrototypes.prettyNames(PROTOTYPE) + ")");}
	}
	
	public TwitterTuples(String name, String topic) throws TwitterException {
		this.name = name;
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey("M1COCjERiQlZHEpro5Aoxg")
			   .setOAuthConsumerSecret("XgVmCNShzpfKG1LhDUxwG5Z6nzzhO2xOHkRfqpwsnKc")
			   .setOAuthAccessToken("18570099-nPaF5WO2h0XGRsvgeZSP6VPhXcKNUcSaIQkm4eM")
			   .setOAuthAccessTokenSecret("Y8F3NcialrzXWou4OqAgrdYKvWMCnsS8WRd6INFuLtY");
		Configuration config = builder.build();
		
		twitter = new TwitterStreamFactory(config).getInstance();
		twitter.addListener(listener);
		if (topic.equals("")) {
			twitter.sample();
		} else {
			FilterQuery query = new FilterQuery();
			query.track(topic.split("\\s*,\\s*"));
			twitter.filter(query);
		}
	}
	
	public SourcedTuple next() {		
		Status s;
		synchronized(queue) {s = queue.poll();}
		if (s == null) {return null;}
				
		Object[] vals = new Object[PROTOTYPE.size()];
		vals[0] =s.getText();
		vals[1] =s.getUser().getName();
		vals[2] = new ArrayTuple(s.getHashtagEntities());
		vals[3] = calcGeo(s);
		vals[4] = s.getPlace() != null ? s.getPlace().getFullName() : null;
		vals[5] =s.getCreatedAt();
		
		PrototypedArrayTuple t = new PrototypedArrayTuple(PROTOTYPE, vals);
		return new SourcedTuple.Wrapper(name, t);
	}

	
	private static String[] LOCATION_NAMES = new String[]{"lat", "lon"};
	private PrototypedArrayTuple calcGeo(Status s) {
		if (s.getGeoLocation() !=  null) {
			return new PrototypedArrayTuple(LOCATION_NAMES, new Object[]{s.getGeoLocation().getLatitude(), s.getGeoLocation().getLongitude()});
		} else if (s.getPlace() != null && s.getPlace().getBoundingBoxCoordinates() != null) {
			GeoLocation[][] locs = s.getPlace().getBoundingBoxCoordinates();
			return new PrototypedArrayTuple(LOCATION_NAMES, new Object[]{locs[0][0].getLatitude(), locs[0][0].getLongitude()});
		} else {
			return null;
		}
	}
	
	
	public boolean hasNext() {return true;}

	public void remove() {throw new UnsupportedOperationException();}
	public List<String> getFields() {return Arrays.asList(TuplePrototypes.getNames(PROTOTYPE));}
}
