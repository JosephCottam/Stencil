package stencil.interpreter.guide.samplers;

import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;

public class FlexSampler implements SampleOperator {
	private final StringSampler string = new StringSampler(); //TODO: Add the ability to switch to different categorical types	
	private final NumericSampler numeric = new NumericSampler();	
	
	@Override
	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		if (seed.isCategorical()) {
			return string.sample(seed.getCategorical(), details);
		} else if (seed.isContinuous()) {
			return numeric.sample(seed.getContinuous(), details);
		} else {
			List<Tuple> cat = string.sample(seed.getCategorical(), details);
			List<Tuple> cont = numeric.sample(seed.getContinuous(), details);
			cont.addAll(cat);
			return cont;
		}		
	}

}
