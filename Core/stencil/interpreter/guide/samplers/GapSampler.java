package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.modules.stencilUtil.MonitorSegments;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;

/**Produces a description of the gaps in a sequence.
 * The resulting tuples are (start, size) pairs.
 */
public class GapSampler implements SampleOperator {
	public GapSampler() {}

	@Override
	public List<Tuple> sample(SampleSeed sd, Specializer spec) {		
		ArrayList sample = new ArrayList();

		if (!sd.isSegments()) {throw new RuntimeException("Can only be used with segments sample.");}
		if (sd.size() <2 ) {return sample;}//Insufficient seed -> No sample

		SampleSeed<MonitorSegments.Segment> seed = sd;
		sample.add(ArrayTuple.from(seed.get(0).start, 0));
		for (int i=0; i< seed.size()-1;i++) {
			MonitorSegments.Segment s1 = seed.get(i);
			MonitorSegments.Segment s2 = seed.get(i);
			sample.add(ArrayTuple.from(s1.end, s2.start-s1.end));
		}
		sample.add(ArrayTuple.from(seed.get(seed.size()-1).end, 0));
		return sample;
	}
}

 	