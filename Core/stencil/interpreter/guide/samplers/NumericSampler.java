package stencil.interpreter.guide.samplers;

import static stencil.parser.ParserConstants.FALSE_STRING;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.types.Converter;

public class NumericSampler implements SampleOperator {
	public static final String SAMPLE_INTEGERS = "round";
	public static final String SAMPLE_COUNT = "count";

	public List<Tuple> sample(SampleSeed seed, Specializer spec) {
		Iterable source;
		int sourceSize;
		
		if (!seed.isRange()) {
			source = seed;
			sourceSize = seed.size();
		} else {
			double min = ((Number) seed.get(0)).doubleValue();
			double max = ((Number) seed.get(1)).doubleValue();
			int tickCount = 
				spec.getMap().containsKey(SAMPLE_COUNT) ?
				Converter.toInteger(spec.getMap().get(SAMPLE_COUNT)) : 10;
			boolean useIntegers = 
				spec.getMap().containsKey(SAMPLE_INTEGERS) &&
				!spec.getMap().get(SAMPLE_INTEGERS).toString().toUpperCase().equals(FALSE_STRING);
			source = buildRange(max, min, tickCount, useIntegers);
			sourceSize = ((List) source).size();
		}

		List<Tuple> sample = new ArrayList(sourceSize);
		for (Object sv: source) {sample.add(Converter.toTuple(sv));}
		return sample;

	}

	private static List<Number> buildRange(double max, double min, int tickCount, boolean useIntegers) {
		double range = niceNum(max-min, false);							//'Nice' range
		double spacing = niceNum(range/(tickCount-1), true);			//'Nice' spacing;
		if (spacing < 1) {spacing =1;}									//Ensure some spacing occurs
		double graphMin = Math.floor(min/spacing) * spacing;			//Smallest value on the graph
		double graphMax = Math.ceil(max/spacing) * spacing;				//Largest value on the graph
		List<Number> nums = new ArrayList();
		
		for (double v=graphMin; v<(graphMax+.5*spacing); v+=spacing) {
			if (useIntegers) {nums.add((int) v);}
			else {nums.add(v);}
		}
		
		return nums;
	}
	

	
	/**Finds a multiple of 1,2 or 5 or a power of 10 near the passed number.
	 * 
	 * From: Graphic Gems, "Nice Numbers for Graph Labels," by Paul Heckbert*/
	private static double niceNum(double num, boolean round) {
		int exp;
		double f;
		double nf;
		
		exp = (int) Math.floor(Math.log10(num));
		f = num/Math.pow(10, exp);
		if (round) {
			if (f< 1.5) {nf=1;}
			else if (f<3) {nf=2;}
			else if (f<7) {nf=5;}
			else {nf=10;}
		} else {
			if (f<=1) {nf=1;}
			else if (f<=2) {nf=2;}
			else if (f<=5) {nf=5;}
			else {nf=10;}
		}		
		return (float) (nf*Math.pow(10, exp));
	}
	
}

