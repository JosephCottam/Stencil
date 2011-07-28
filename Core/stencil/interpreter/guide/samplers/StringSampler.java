package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.Singleton;

public class StringSampler implements SampleOperator {
	public StringSampler(String type) {}
	
	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		if (seed.isContinuous()) {throw new IllegalArgumentException("Can only do cateogrical string samples.");}
		
		List<Tuple> sample = new ArrayList(seed.size());
		for (Object v: seed) {
			if (v.getClass().isArray()) {
				sample.add(new ArrayTuple((Object[]) v));
			} else {
				sample.add(Singleton.from(v.toString()));
			}
		}
		return sample;
	}
}
