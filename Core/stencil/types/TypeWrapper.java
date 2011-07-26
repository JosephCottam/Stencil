package stencil.types;

import stencil.tuple.Tuple;

/**Class for the exchanging tuples and java classes.
 * 
 * @author jcottam
 *
 */
public interface TypeWrapper<T extends Tuple> {
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
