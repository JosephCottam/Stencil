package stencil.interpreter.tree;

import java.util.*;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MultiResultTuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;

public class Target {
	private final TargetTuple tt;
	
	public Target(TargetTuple tt) {this.tt= tt;}
	
	public TuplePrototype prototype() {return tt.asPrototype();}

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
			if (source.get(i) instanceof MultiResultTuple) {
				MultiResultTuple result = (MultiResultTuple) source.get(i);
				if ((maxSize > 0 && result.size() != maxSize)) {
					throw new RuntimeException("Cannot finalize tuple results with multiple inconsistent lengths greater than 1.  Data: " + Arrays.deepToString(Tuples.toArray(source)));
				}
				maxSize = Math.max(maxSize, result.size());
			}
		} 
		if (maxSize != -1) {return finalizeAll(source, maxSize);}
		else {return finalizeOne(source);}
	}

	
	private final Tuple finalizeOne(Tuple source) {
		final Object[] values = new Object[tt.size()];
		for (int i=0; i< values.length; i++) {values[i] = source.get(i);}
		return new PrototypedArrayTuple(tt.asPrototype(), values);
	}


	//How to finalize if there is a map-merge tuple involved 
	private final Tuple finalizeAll(Tuple sources, int size) {
		final List[] values = new List[size];
		for (int i=0; i< values.length; i++) {values[i] = new ArrayList();}

		for (int field=0; field< sources.size(); field++) {
			Object value = sources.get(field);
			if (value instanceof MultiResultTuple) {
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
		return new MultiResultTuple(results);
	}
}
