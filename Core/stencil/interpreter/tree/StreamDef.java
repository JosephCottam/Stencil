package stencil.interpreter.tree;

import java.util.LinkedList;
import java.util.Queue;

import stencil.interpreter.TupleStore;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;


//TODO: Unify with Layer (requires proper dispatcher)
public class StreamDef implements TupleStore {
	/**Value used to divide groups of tuples in the queue.*/
	public static final SourcedTuple DIVIDER = new SourcedTuple.Wrapper("****DIVIDER****", Tuples.EMPTY_TUPLE);

	private final Queue<SourcedTuple> queue = new LinkedList();		//TODO: Move into a proper dispatcher

	private final String name;
	private final Consumes[] groups;
	private final TuplePrototype prototype;

	public StreamDef(String name, Consumes[] groups,  TuplePrototype prototype) {
		super();
		this.name = name;
		this.groups = groups;
		this.prototype = prototype;
	}
	
	public String getName() {return name;}
	public Consumes[] getGroups() {return groups;}
	
	public boolean canStore(Tuple t) {return t.size() == prototype.size();}
	public void store(Tuple t) {offer(t);}	
	public void offer(SourcedTuple t) {queue.offer(t);}
	public void offer(Tuple t)  {
		if (t instanceof SourcedTuple) {offer((SourcedTuple) t);}
		offer(new SourcedTuple.Wrapper(name, t));	
	}	
	
	public SourcedTuple poll() {return queue.poll();}
	public boolean isDivider() {return queue.peek() == DIVIDER;}
	public boolean isEmpty() {return queue.size() == 0;}
}
