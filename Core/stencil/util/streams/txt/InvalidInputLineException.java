package stencil.util.streams.txt;

public class InvalidInputLineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidInputLineException(String message) {this(message, null);}
	public InvalidInputLineException(String message, Exception cause) {super(message, cause);}
}
