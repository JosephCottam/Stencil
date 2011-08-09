package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.modules.stencilUtil.MonitorSegments;
import stencil.tuple.Tuple;
import stencil.types.Converter;

/**Produces a set of samples from a given set of segments.
 * Segments can be sub-sampled if the "subSample" key is in the specializer,
 * parameters on this are the same as the numeric sampler, 
 * except the sample will always be 'tight' and include the start and end of the segment.
 * Additionally, parameters are applied on a per-segment basis.
 * 
 * If sub-sampling is not requested, the sample is a sequence of tuples
 * that alternating define the start and end of the segment.
 */
public class SegmentSampler implements SampleOperator {
	public static final String SUBSAMPLE_KEY = "subSample";
	
	private final NumericSampler subSampler = new NumericSampler();
	
	
	public SegmentSampler() {}

	public List<Tuple> sample(SampleSeed sd, Specializer spec) {
		
		if (!sd.isSegments()) {throw new RuntimeException("Can only be used with segments sample.");}
		boolean subSample = (spec.containsKey(SUBSAMPLE_KEY) && Converter.toBoolean(spec.get(SUBSAMPLE_KEY)));		
		if (subSample) {return subSample(sd, spec);}
		else {return simpleSample(sd);}
	}
	
	public List<Tuple> subSample(SampleSeed<MonitorSegments.Segment> seed, Specializer spec) {
		ArrayList sample = new ArrayList();
		if (seed.size() <2 ) {return sample;}//Insufficient seed -> No sample
		
		for (MonitorSegments.Segment s: seed) {
			SampleSeed subSeed = new SampleSeed(SampleSeed.SeedType.CONTINUOUS, Arrays.asList(s.start, s.end));
			sample.addAll(subSampler.sample(subSeed, spec));
		}
		return sample;
	}
	
	public List<Tuple> simpleSample(SampleSeed<MonitorSegments.Segment> seed ) {
		ArrayList sample = new ArrayList();
		if (seed.size() <2 ) {return sample;}//Insufficient seed -> No sample

		for (MonitorSegments.Segment s: seed) {
			sample.add(Converter.toTuple(s.start));
			sample.add(Converter.toTuple(s.end));
		}
		return sample;
	}
}

 	