package stencil.explore.util;

/**Interface for reporting textual information to the user/programmer.
 * For consistency, the reporter calls should go before any direct calls
 * to System.out, System.err, printStackTrace, etc. This keeps message
 * order consistent for reporters that echo to the System streams.
 * 
 */
public interface MessageReporter {
	/**Add a formated message to the report.*/
	public void addMessage(String format, Object...objects); 

	/**Add a formated error to the report.*/
	public void addError(String format, Object...objects); 
	
	/**Clear messages (if possible).  If this is not possible in the given
	 * reporter, this should simply return.
	 */
	public void clear();
}
