package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import stencil.display.Guide2D;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.types.Converter;


public class NumericSampler implements SampleOperator {
	/**Should values be rounded in sampling?*/
	public static final String SAMPLE_INTEGERS = "round";
	
	/**How many samples should be produced?
	 * This is mutually exclusive with indicating the sample stride.
	 */
	public static final String SAMPLE_COUNT = "count";
	
	/**What should the spacing between samples be.
	 * For example, stride of 10 means that all values will be 10 units apart.
	 */
	public static final String SAMPLE_STRIDE = "stride";

	/**For log samples, what should the log base be? (Default is 10.)**/
	public static final String SAMPLE_BASE = "base";

	/**Should the sample make sure it contains the zero point?
	 * If this key is present (regardless of value), the zero point will be ignored.
	 */
	public static final String TIGHT = "tight";

	public List<Tuple> sample(SampleSeed seed, Specializer spec) {
		Iterable source;
		int sourceSize;
		
		
		if (!seed.isContinuous()) {throw new RuntimeException("Can only use continuous seed for numeric samples.");}
		if (seed.size() <=1) {return new ArrayList();}//No seed -> No sample

		boolean log = spec.get(Guide2D.SAMPLE_KEY).equals("LOG");

		double min = ((Number) seed.get(0)).doubleValue();
		double max = ((Number) seed.get(1)).doubleValue();
		if (spec.containsKey("min")) {min = Converter.toDouble(spec.get("min"));}
		if (spec.containsKey("max")) {max = Converter.toDouble(spec.get("max"));}
		
		//Include the zero point if the sample is not intended to be 'tight'
		if (!spec.containsKey(TIGHT)) {
			if (min > 0 &&  max> 0) {min =0;}
			if (max < 0 && min <0) {max =0;}
		}
		
		
		int tickCount = 10;
		boolean useIntegers = (spec.containsKey(SAMPLE_INTEGERS) && !Converter.toBoolean(spec.get(SAMPLE_INTEGERS)));

		if (spec.containsKey(SAMPLE_STRIDE) && spec.get(SAMPLE_STRIDE) != null) {
			double stride = Converter.toDouble(spec.get(SAMPLE_STRIDE));
			tickCount = (int) Math.ceil((max-min)/stride);
		} else if (spec.containsKey(SAMPLE_COUNT)) {
			tickCount =  Converter.toInteger(spec.get(SAMPLE_COUNT));
		}
		
		if (!log) {
			source = linearSample(max, min, tickCount, useIntegers);
		} else {
			double base = spec.containsKey("base") ? Converter.toDouble(spec.get("base")) : 10;
			source = logSample(max, min, tickCount, base); 
		}
		
		sourceSize = ((List) source).size();		
		List<Tuple> sample = new ArrayList(sourceSize);
		for (Object sv: source) {sample.add(Converter.toTuple(sv));}
		return sample;

	}

	private static List<Number> logSample(double max, double min, int tickCount, double base) {
		if (base == 0) {throw new IllegalArgumentException("Attempt to use base of 0 in sampling.");}

		List<Number> values = new ArrayList();
		double raiseTo = Math.log(max+1)/Math.log(base);
		int maxPow = (int) Math.ceil(raiseTo);
		
		
		for (int i=0; i < maxPow; i++) {
			double v = Math.pow(base, i);
			values.add(v);
		}
		return values;
	}
	
	private static List<Number> linearSample(double max, double min, int tickCount, boolean useIntegers) {
		float range = niceNum(max-min, false);							//'Nice' range
		float spacing = niceNum(range/(tickCount-1), true);				//'Nice' spacing;
		if (spacing < Double.MIN_NORMAL) {spacing =1;}					//Ensure some spacing occurs
		float graphMin = (float) Math.floor(min/spacing) * spacing;		//Smallest value on the graph
		float graphMax = (float) Math.ceil(max/spacing) * spacing;		//Largest value on the graph
		SortedSet<Number> nums = new TreeSet();
		
		for (float v=graphMin; nums.size() == 0 || nums.last().doubleValue() < graphMax; v+=spacing) {
			if (useIntegers) {nums.add((long) v);}
			else {nums.add(v);}
		}
		
		return new ArrayList(nums);
	}
	

	
	/**Finds a multiple of 1,2 or 5 or a power of 10 near the passed number.
	 * 
	 * From: Graphic Gems, "Nice Numbers for Graph Labels," by Paul Heckbert
	 * TODO: Change to using Talbots InfoVis 2010 numbers
	 * */
	private static float niceNum(double num, boolean round) {
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

 	