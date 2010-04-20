package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

public class ColorSampler implements SampleOperator {
	/**Categorical: Echos colors given in the descriptor
	 * Continuous: Samples a path through HSV space that connects all colors listed
	 * 
	 * TODO: Implement continuous sample in color
	 */
	public List<Tuple> sample(SampleSeed seed, Specializer spec) {
		if (!seed.isRange()) {
			List<Tuple> sample = new ArrayList(seed.size());
			for (Object c: seed) {sample.add(Converter.toTuple(c, new ArrayTuple()));}
			return sample;
		} else {
			throw new Error("Cannot produce a continous sample on color data.");
		}
	}
}
