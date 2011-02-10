package stencil.interpreter.tree;

import java.util.*;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

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
		int maxSize =-1;
		
		//HACK: Horrible, horrible!!!  Why do all this work EVERY TIME on the CHANCE that there is a merge???
		for (int i=0; i< source.size(); i++) {
			if (source.get(i) instanceof MapMergeTuple) {
				if ((maxSize > 0 && source.size() != maxSize)) {
					throw new RuntimeException("Cannot finalize tuple results with multiple inconsistent lengths greater than 1.  Data: " + Arrays.deepToString(Tuples.toArray(source)));
				}
				maxSize = Math.max(maxSize, ((MapMergeTuple) source.get(i)).size());
			}
		} 
		if (maxSize != -1) {return finalizeAll(source, maxSize);}
		else {return finalizeOne(source);}
	}

	
	private final Tuple finalizeOne(Tuple source) {
		String[] fields = TuplePrototypes.getNames(proto);
		Object[] values = new Object[fields.length];

		int size = fields.length;
		for (int i=0; i< size; i++) {values[i] = source.get(i);}

		return new PrototypedTuple(fields, values);
	}


	//How to finalize if there is a map-merge tuple involved 
	private final Tuple finalizeAll(Tuple sources, int size) {
		final List[] values = new List[size];
		for (int i=0; i< values.length; i++) {values[i] = new ArrayList();}

		for (int field=0; field< sources.size(); field++) {
			Object value = sources.get(field);
			if (value instanceof MapMergeTuple) {
				Tuple source = ((Tuple) value);
				for (int result=0; result<values.length; result++) {
					Object[] resultValues = Tuples.toArray((Tuple) source.get(result));
					values[result].addAll(Arrays.asList(resultValues));
				}
			} else {
				for (int result=0; result<values.length; result++) {
					values[result].add(value);
				}				
			}
		}
		
		final Tuple[] results = new Tuple[values.length];
		for (int i=0; i< results.length; i++) {
			Object[] vals = values[i].toArray(new Object[values[i].size()]);
			results[i] = finalizeOne(new ArrayTuple(vals));
		}
		return new MapMergeTuple(results);
	}
}
