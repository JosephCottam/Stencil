package stencil.tuple;

import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;


/***Tuple that also has a prototype. 
 *  Prototypes enable name-based operations, but are not required in many parts 
 *  of the runtime.  
 *  
 * @author jcottam
 */
public interface PrototypedTuple<T extends TupleFieldDef> extends Tuple {
	/**Get a listing of all fields known by this tuple (even if they are not set).
	 * The order of the list corresponds to the index of the field number.
	 * */
	public abstract TuplePrototype<T> prototype();
	
	/**Returns the object as stored under the name.
	 * @throws InvalidNameException The name passed is not valid for this tuple.*/
	public abstract Object get(String name) throws InvalidNameException;
}

