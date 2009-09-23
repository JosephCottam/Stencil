package stencil.operator.module;

import java.util.List;

import stencil.operator.module.OperatorData.OpType;

public interface FacetData {
	/**What is the name of this facet?*/
	public String getName();
	
	/**What is the return tuple field set?*/
	public List<String> tupleFields();
	
	/**What type is the given facet?*/
	public OpType getFacetType();
}
