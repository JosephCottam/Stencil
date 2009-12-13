package stencil.operator.module;

import stencil.operator.module.OperatorData.OpType;
import stencil.tuple.prototype.TuplePrototype;

public interface FacetData {
	/**What is the name of this facet?*/
	public String getName();
	
	/**What is the return tuple field set?*/
	public TuplePrototype getPrototype();
	
	/**What type is the given facet?*/
	public OpType getFacetType();
}
