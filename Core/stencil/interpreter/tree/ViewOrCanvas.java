package stencil.interpreter.tree;

import stencil.interpreter.TupleStore;
import stencil.tuple.Tuple;

public class ViewOrCanvas implements TupleStore {
	private final TupleStore impl;
	private final String name;
	private final Specializer spec;
	private final Consumes[] groups;
	
	public ViewOrCanvas(TupleStore impl, String name, Specializer spec, Consumes[] groups) {
		this.impl = impl;
		this.name = name;
		this.groups = groups;
		this.spec = spec;
	}
	
	@Override
	public String getName() {return name;}
	public Consumes[] getGroups() {return groups;}
	public TupleStore implementation() {return impl;}
	public Specializer specializer() {return spec;}
		
	/**Can the object be stored in the underlying layer?
	 * Minimum requirements are (1) t is not null and (2) t has a prototype.
	 */
	@Override
	public boolean canStore(Tuple t) {return impl.canStore(t);}

	@Override
	public void store(Tuple t) {impl.store(t);}
}
