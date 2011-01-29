package stencil.interpreter.tree;

import stencil.tuple.Tuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.types.Converter;

public class Target {
	private final TuplePrototype proto;
	
	public Target(TuplePrototype proto) {
		this.proto = proto;
	}

	public TuplePrototype prototype() {return proto;}
	
	/**Create a new tuple where the names are take from the tuple prototype and
	 * values are take from the source.
	 * 
	 * The resulting tuple should is able to update a real target 
	 * (such as a glyph, view or canvas) via merge.
	 */
	public final Tuple finalize(Tuple source) {
		if (source instanceof MapMergeTuple) {
			Tuple[] results = new Tuple[source.size()];
			for (int i=0; i< source.size(); i++) {
				results[i] = finalizeOne(Converter.toTuple(source.get(i)));
			}
			return new MapMergeTuple(results);
		} else {
			return finalizeOne(source);
		}
	}
	
	private final Tuple finalizeOne(Tuple source) {
		String[] fields = TuplePrototypes.getNames(proto);
		Object[] values = new Object[fields.length];

		int size = fields.length;
		for (int i=0; i< size; i++) {values[i] = source.get(i);}

		Tuple rv = new PrototypedTuple(fields, values);
		return rv;
	}
	
}
