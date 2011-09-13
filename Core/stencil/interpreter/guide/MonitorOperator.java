package stencil.interpreter.guide;

import java.lang.reflect.Array;

import stencil.interpreter.Viewpoint;
import stencil.tuple.instances.MultiResultTuple;
import stencil.types.Converter;

public interface MonitorOperator<T extends MonitorOperator> extends Viewpoint<T> {
	/**Get information about the values that have been seen.
	 * This need not be a comprehensive sample (thought it may be,
	 * especially for categorical variables of unknown domain).
	 * The sample operator is responsible for producing the final
	 * sample, based on the seed.
	 */
	public SampleSeed getSeed();
	
	/**State identifier for the seed operator.**/
	public int stateID();
	
	
	public static final class Util {
		/**Converts input tuple to a list values of the passed class.
		 * Proper handling of multi-result tuples for monitor operators indicates
		 *	that each of the multi-results should be treated as a separate result.
		 * Therefore, this method converts a multi-result tuple to an array of many tuples,
		 * otherwise it just returns an array containing the original input itself.
		 * **/
		public static <T> T[] values(Object input, Class<T> clss) {
			Object result;
			if (input instanceof MultiResultTuple) {
				MultiResultTuple mr = (MultiResultTuple) input;
				result = Array.newInstance(clss, mr.size());				

				for (int i=0; i< mr.size(); i++) {
					Array.set(result, i, Converter.convert(mr.getTuple(i).get(0), clss));
				}
			} else {
				result = Array.newInstance(clss, 1);
				Array.set(result, 0, Converter.convert(input, clss));
			}
			return (T[]) result;
		}		
	}
}
