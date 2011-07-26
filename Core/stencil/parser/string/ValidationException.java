package stencil.parser.string;

public class ValidationException extends RuntimeException {
	public ValidationException(Throwable cause, String msg, Object...args) {super(String.format(msg, args), cause);}
	public ValidationException(String msg, Object...args) {super(String.format(msg, args));}
	public ValidationException(String msg) {super(msg);}
	public ValidationException(String msg, Throwable cause) {super(msg, cause);}
}
