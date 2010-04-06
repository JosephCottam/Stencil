package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;

public class StringSampler implements SampleOperator {

	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		if (seed.isRange()) {throw new IllegalArgumentException("Can only do cateogrical string samples.");}
		
		List<Tuple> sample = new ArrayList(seed.size());
		for (Object v: seed) {
			if (v.getClass().isArray()) {
				sample.add(new ArrayTuple((Object[]) v));
			} else {
				sample.add(new ArrayTuple(v.toString()));
			}
		}
		return sample;
	}
}
