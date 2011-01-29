package stencil.interpreter;

import stencil.tuple.InvalidNameException;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.prototype.TuplePrototype;


/**NoOutput is used to support operations that are executed, 
 * that do not error BUT do not produce their "expected" output.
 * For example, a range has not yet been filled.  
 * Therefore, the ranged operator may not return a value and all transformations
 * depending on that value cannot execute.  HOWEVER, other stateful operations may
 * (lexically) follow the application that do not depend on the range result  
 * which should still execute (according to the link semantics).
 * 
 * NoOutput.Signal is the exception that indicates that no output was produced.
 * NoOutput.Tuple is a tuple that generates the Signal when accessed.  
 *    An operator that does not produce output may either return NoOutput.TUPLE 
 *    or throw NoOutput.Signal; the effect is the same.
 * 
 * @author jcottam
 *
 */
public final class NoOutput {
	/**Indicate that No output is to be produced, 
	 * but that evaluation should continue (thus, it is a signal, not an exception); 
	 */
	
	public static final class Signal extends RuntimeException {}
	
	/**Tuple that will generate a NoOutput.Signal anytime it is accessed.*/
	public static final stencil.tuple.Tuple TUPLE = new stencil.tuple.Tuple() {
		public Object get(String name) throws InvalidNameException {throw new Signal();}
		public Object get(int idx) throws TupleBoundsException {throw new Signal();}
		public TuplePrototype getPrototype() {throw new Signal();}
		public boolean isDefault(String name, Object value) {throw new Signal();}
		public int size() {throw new Signal();}		
	};

}
