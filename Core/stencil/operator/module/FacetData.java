package stencil.operator.module;

import stencil.operator.module.OperatorData.OpType;
import stencil.tuple.prototype.TuplePrototype;

public interface FacetData {
	/**Canonical name of attribute to indicate that an operator is a function.*/
	public static final String FUNCTION_ATTRIBUTE = "function";

	/**Canonical name of attribute to indicate the facet type.*/
	public static final String TYPE_ATTRIBUTE = "type";
	
	/**Canonical name of attribute to indicate the facet prototype.*/
	public static final String PROTOTYPE_ATTRIBUTE = "prototype";
	
	
	/**What is the name of this facet?*/
	public String getName();
	
	/**What is the return tuple field set?*/
	public TuplePrototype getPrototype();
	
	/**What type is the given facet?*/
	public OpType getFacetType();
	
	/**Get an additional attribute value.
	 * If an attribute is unknown, this must return null.
	 * */
	public String getAttribute(String key);
}
