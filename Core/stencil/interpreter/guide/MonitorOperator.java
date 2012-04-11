package stencil.interpreter.guide;

import stencil.module.operator.StencilOperator;

public interface MonitorOperator<T extends MonitorOperator> extends StencilOperator<T> {
	/**Get information about the values that have been seen.
	 * This need not be a comprehensive sample (thought it may be,
	 * especially for categorical variables of unknown domain).
	 * The sample operator is responsible for producing the final
	 * sample, based on the seed.
	 */
	public SampleSeed getSeed();
	
	/**State identifier for the seed operator.**/
	public int stateID();
}
