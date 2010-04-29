package stencil.parser.tree;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

import org.antlr.runtime.Token;

import stencil.interpreter.TupleStore;
import stencil.parser.string.StencilParser;
import stencil.tuple.ArrayTuple;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;

public class StreamDef extends StencilTree implements TupleStore, ContextNode {
	/**Value used to divide groups of tuples in the queue.*/
	public static final SourcedTuple DIVIDER = new SourcedTuple.Wrapper("****DIVIDER****", new ArrayTuple());

	private Queue<SourcedTuple> queue = new LinkedList();	
	private List<Consumes> groups;

	public StreamDef(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}
	public List<Rule> getDefaults() {
		return (List<Rule>) findChild(StencilParser.LIST, "Defaults");
	}
	
	public List<Consumes> getGroups() {
		if (groups == null) {groups = (List<Consumes>) findChild(StencilParser.LIST, "Consumes");}
		return groups;
	} 
	
	public boolean canStore(Tuple t) {return t.size() == getPrototype().size();}
	public void store(Tuple t) {offer(t);}	
	public void offer(Tuple t)  {
		if (t instanceof SourcedTuple) {offer((SourcedTuple) t);}
		offer(new SourcedTuple.Wrapper(getName(), t));	
	}	
	
	public void offer(SourcedTuple t) {
		queue.offer(t);
	}
	
	public SourcedTuple poll() {return queue.poll();}
	public boolean isDivider() {return queue.peek() == DIVIDER;}
	public boolean isEmpty() {return queue.size() == 0;}
}
