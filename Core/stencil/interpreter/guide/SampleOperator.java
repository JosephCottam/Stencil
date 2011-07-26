package stencil.interpreter.guide;

import java.util.List;

import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;


public interface SampleOperator {
	public static final String SAMPLE_PREFIX = "#Sample";

	public static final class Util { 
		private Util() {}
		
		/**Tuple prototype is similar for all sampler sets: #Sample + i
		 * One i for each sampler considered.
		 */
		public static final TuplePrototype prototype(final int samplers) {
			final String[] names = new String[samplers];
			for (int i=0; i<samplers;i ++) {
				names[i] = SAMPLE_PREFIX + (i==0 ? "" : i);
			}
			return new TuplePrototype(names);
		}
	}

	
	/**Generate a sample based on the seed values and the details.
	 * The values of details that are actually paid attention to 
	 * depends on the sample operator itself. 
	 * 
	 */
	public List<Tuple> sample(SampleSeed seed, Specializer details);	
}
