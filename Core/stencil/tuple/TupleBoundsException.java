package stencil.tuple;

import java.util.Arrays;

import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;

/**Thrown to indicate that an out-of-bounds tuple index was requested
 *
 * @author jcottam
 *
 */
public class TupleBoundsException extends IndexOutOfBoundsException {
	private static final long serialVersionUID = 1L;
	public TupleBoundsException(int idx, int size) {
		super(String.format("Index %1$d invalid in tuple of size %2$d.", idx, size));
	}
	
	public TupleBoundsException(int idx, TuplePrototype prototype) {
		super(String.format("Index %1$d invalid in tuple of size %2$d (valid fields: %3$s).", 
				idx, prototype.size(), Arrays.deepToString(TuplePrototypes.getNames(prototype))));		
	}
	
	public TupleBoundsException(int idx, Tuple t) {
		super(String.format("Index %1$s invalid in tuple of size %2$d (tuple: %3$s).",
				idx, t.size(), t.toString()));	
	}
}
