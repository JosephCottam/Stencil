package stencil.tuple.stream;

public class InvalidTupleException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidTupleException(String message) {this(message, null);}
	public InvalidTupleException(String message, Exception cause) {super(message, cause);}
}
