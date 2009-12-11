package stencil.tuple;

import static java.lang.String.format;

/**Indicates that some error occurred during type validation.
 * 
 * Errors include (but are not limited to):
 * 	   Mismatch of types list to values list
 *     No known conversion between value and type registered with the converter framework
 * */ 
public class TypeValidationException extends RuntimeException {
	public TypeValidationException(String message) {super(message);}
	public TypeValidationException(String message, Exception e) {super(message,e);}
	public TypeValidationException(Class type, Object value) {this(type, value, null);}
	public TypeValidationException(Class type, Object value, Exception e) {
		super(format("could not validate %1$s to type %2$s.", value, type.getCanonicalName()), null);
	}

}
