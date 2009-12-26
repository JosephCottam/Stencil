package stencil.util.streams.txt;

import java.io.BufferedReader; 
import java.util.Arrays;
import java.util.List;

/**Means to get the next value from a stream.
 * 
 * @author jcottam
 *
 */
public interface NextChannel {
	/**Exception used to indicate a validation problem.*/
	public class FileValidationException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public FileValidationException(List<String> expected, int found) {
			this(expected, found, null);
		}
		public FileValidationException(List<String> expected, int found, Exception cause) {
			super(String.format("Column count does not match the length of the labels list (found %1$s, expected %2$s)", found, Arrays.deepToString(expected.toArray())), cause);
		}
	}
	
	/**What is the next tuple?*/
	public List<Object> next(BufferedReader source) throws Exception;
	
	/**Does the source validate according to the needs of this channel?
	 * @return
	 */
	public void validate(BufferedReader source) throws Exception;
}
