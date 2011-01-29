package stencil.interpreter.tree;

import stencil.display.DisplayLayer;
import stencil.interpreter.TupleStore;
import stencil.parser.ParserConstants;
import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedTuple;

//TODO: Merge with StreamDef
public class Layer implements TupleStore {
	private final DisplayLayer impl;
	private final String name;
	private final Consumes[] groups;
	public Layer(DisplayLayer impl, String name, Consumes[] groups) {
		super();
		this.impl = impl;
		this.name = name;
		this.groups = groups;
	}
	
	public String getName() {return name;}
	public Consumes[] getGroups() {return groups;}
	public DisplayLayer getDisplayLayer() {return impl;}
	
	public void store(Tuple t) {impl.makeOrFind(t);}
	public boolean canStore(Tuple t) {
		return t instanceof PrototypedTuple &&
		((PrototypedTuple) t).getPrototype().contains(ParserConstants.SELECTOR_FIELD);
	}
}
