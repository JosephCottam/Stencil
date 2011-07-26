package stencil.module.operator;


import stencil.interpreter.Viewpoint;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.OperatorData;


/**An operator is an object that can 
 * hand out method objects to be appropriately invoked as facets.
 * Facets are requested by name.  
 * 
 * Each operator should provide a map and query facet.
 * A stateID facet is required for stateful operators.
 * 
 * Operators instances may be specialized, and the specialization will
 * be reflected in all roles.  Operators instances of the same base type
 * must not share writeable memory or must be labeled as Opaque. 
 *  
 * @author jcottam
 *
 */
public interface StencilOperator<T extends StencilOperator> extends Viewpoint<T> {
	/**Name of the facet used by default in contexts where mutation is permitted.*/
	public static final String MAP_FACET ="map";

	/**Name of the facet used by default in contexts where mutation is NOT permitted.*/
	public static final String QUERY_FACET ="query";
	
	/**Facet used to get the ID of the current state.
	 * This is used to determine if update operations are required.
	 */
	public static final String STATE_ID_FACET = "stateID";
	
	/**Retrieve an invokable object.  This is a combined method and target.
	 * IllegalArgumentException is thrown when the facet is not know.
	 * */
	public Invokeable getFacet(String facet) throws UnknownFacetException;
	
	/**Retrieve the operator data for the current operator.*/ 
	public OperatorData getOperatorData();
	
	/**How is this operator identified?  Operators are registered in their modules under their name.*/
	public String getName();
	
	/**Return a new operator that is functionally identical, but with Stencil-runtime state
	 * reset.  Any specialization-determined state should be copied to the new operator.
	 * 
	 * This is an optional operation, required by Split support.  If an operator does not support
	 * split, it should throw an UnsupportedOperationException.
	 * 
	 * This is similar, but not identical to clone as clone copies ALL runtime state where
	 * duplicate produces a new instance.
	 * 
	 * A possible implementation would be to clone the implementing object immediately upon
	 * 	construction and keep that pristine clone in private storage.  When duplicate is
	 * called, the clone is then cloned again and the new replicate is returned.  
	 * This is not necessarily an efficient way to implement duplicate, but it is
	 * would satisfy the semantics of duplicate. True mathematical functions with no 
	 * specialization arguments may return themselves from this method (no duplication required). 
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public StencilOperator duplicate() throws UnsupportedOperationException;
	
	/**A viewpoint is the operator as it current exists.  
	 * Viewpoints are allowed to reflect future updates to the source operator.
	 * They are not allowed to modify state.  The only thing an operator
	 * viewpoint is guaranteed to correctly respond to are 'stateID' and 'query'.
	 * All other facets MAY throw an exception. Query should respond
	 * as if it were being called on the memory state in the operator at
	 * the time the viewpoint was created.
	 * 
	 * @return
	 */
	public T viewpoint();
}
