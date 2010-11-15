package stencil.testUtilities;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**Suppresses/restores output to System.err.
 * This is a state-full operation pair that should only be 
 * in effect when error reporting distracts from interpreting test results
 * (such as attempts to parse known malformed programs).
 * 
 * IT IS NOT SAFE TO:
 *    Call suppress twice without calling restore at least once
 *    Call these methods from multiple threads.
 * 
 * (Multiple calls to restore are fine, as are pre-emptive calls to restore.)
 * 
 * @author jcottam
 *
 */
public final class SuppressOutput {
	private static PrintStream restoreTo;
	
	private static final PrintStream SUPPRESS_WITH = new PrintStream(new ByteArrayOutputStream());
	
	public static void suppress() {
		restoreTo = System.err;
		System.setErr(SUPPRESS_WITH);
	}
	
	public static void restore() {
		if (restoreTo != null) {System.setErr(restoreTo);}
		restoreTo = null;
	}

	
	
}
