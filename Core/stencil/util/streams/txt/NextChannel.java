package stencil.util.streams.txt;

import java.io.BufferedReader; 
import java.util.List;

/**Means to get the next value from a stream.
 * 
 * @author jcottam
 *
 */
public interface NextChannel {
	/**What is the next tuple?*/
	public List<Object> next(BufferedReader source) throws Exception;
	
	/**Does the source validate according to the needs of this channel?
	 * @return
	 */
	public void validate(BufferedReader source) throws Exception, FileValidationException;
}
