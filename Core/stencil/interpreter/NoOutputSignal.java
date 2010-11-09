package stencil.interpreter;

/**The NoOutput indicates that a transfer does not produce output
 * on a given input, but this should be considered a normal part of its
 * behavior (e.g., not an error).
 * 
 * This can occur when a stream definition does not produce output for every input.
 * TODO: Use this for range-not-full-yet
 * 
 * @author jcottam
 *
 */
public class NoOutputSignal extends RuntimeException {
	public NoOutputSignal() {}	
	public NoOutputSignal(String message) {super(message);}	
	public NoOutputSignal(String message, Exception cause) {super(message, cause);}
}
