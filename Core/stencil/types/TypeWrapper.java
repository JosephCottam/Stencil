package stencil.types;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

/**Class for the exchanging tuples and java classes.
 * 
 * @author jcottam
 *
 */
public interface TypeWrapper<T extends Tuple> {
	/**Get an object that expresses the internal state of passed tuple.  
	 * The type of the returned object should be assignment compatible with
	 * the passed class object.  The returned object does not have
	 * to be independent of the passed tuple.  
	 * 
	 * The returned
	 * object should be assignment compatible with the passed class.
	 * This method may assume that the passed class is contained in the
	 * class list returned from 'accepts.'
	 * 
	 * Information may be lost during conversion.
	 * 
	 * @return
	 */
	public <E> E external(Tuple t, TuplePrototype p, Class<E> c);
	
	/**Convert the passed object to the given class.
	 * Should accept objects of the types in the 'accepts' list  
	 * AND any tuple that may be produced by 'toTuple' without throwing an exception. 
	 */
	public <E> E convert(Object v, Class<E> c);
	
	/**Express the passed object as a tuple.
	 * This method may assume that the passed object 
	 * is assignment compatible with a class returned from 'accepts'.
	 * 
	 * Information may be lost during conversion.
	 * This is not part of the normal 'convert' chain, the tuple will
	 * never be explicitly requested from a Convert, however it may be used
	 * during interpretation with the wrapper retrieved from the converter. 
	 * */
	public T toTuple(Object o);

	/**A list of classes this wrapper is targeted at.
	 * This includes the ability to convert to/from a tuple
	 * and perform basic conversions (String, int, double, if applicable).
	 * 
	 * Custom wrappers should not 'apply to' String, int, double or float.
	 * 
	 * 
	 * **/
	public Class[] appliesTo();
}
