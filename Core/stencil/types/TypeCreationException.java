package stencil.types;

import java.util.Arrays;
import java.util.List;

public final class TypeCreationException extends RuntimeException {
	public TypeCreationException(List source) {this(source, null);}
	public TypeCreationException(List source, Exception cause) {this(source.toArray(), cause);}
	 
	/**@param sourrce Source code being parsed.*/
	public TypeCreationException(Object[] source) {this(source, null);}

	/**
	 * @param source Source code being parsed.
	 * @param cause Exception raised while parsing
	 */
	public TypeCreationException(Object[] source, Exception cause) {
		super("Error creating with " + Arrays.deepToString(source) + ".", cause);
	}
}
