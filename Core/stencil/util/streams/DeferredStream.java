package stencil.util.streams;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Stream;
import stencil.tuple.SourcedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;

/**Place-holder stream never reports having stream values.
 * This is used as a default stream type.
 * **/

@Description("Placeholder stream type.  Can be fully constructed, but never produces values.")
@Stream(name="DEFERRED", spec="[]")
public final class DeferredStream implements TupleStream {

	public DeferredStream(String name, TuplePrototype prototype, Specializer spec) {}
	
	@Override
	public boolean hasNext() {return false;}

	@Override
	public SourcedTuple next() {throw new RuntimeException("Attempt to retrieve values from deffered stream instance.");}

	@Override
	public void remove() {throw new UnsupportedOperationException();}

	@Override
	public void stop() {}
}
