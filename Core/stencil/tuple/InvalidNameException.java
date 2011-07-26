package stencil.tuple;

import java.util.*;

import stencil.tuple.prototype.*;

/**Thrown to indicate that a name has been requested that could not be found in the tuple.
 *
 * @author jcottam
 *
 */
public class InvalidNameException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidNameException(String name) {this(name, new String[0]);}
	public InvalidNameException(String name, TuplePrototype prototype) {this(name, TuplePrototypes.getNames(prototype));}
	public InvalidNameException(String name, String[] valid) {this(name, valid, null);}
	
	public InvalidNameException(String name, TuplePrototype prototype, Throwable e) {this(name, TuplePrototypes.getNames(prototype), e);}
	public InvalidNameException(String name, String[] valid, Throwable e) {
		super(String.format("Could not find field of name \"%1$s\" in tuple (valid names: %2$s).", name, Arrays.deepToString(valid)), e);
	}

	public InvalidNameException(String typeName, String name, TuplePrototype prototype) {
		this(String.format("Could not find field of name \"%1$s\" in tuple of type \"%2$s\" (valid names: %3$s).", name, typeName, TuplePrototypes.prettyNames(prototype)));
	}

}
