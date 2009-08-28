package stencil.legend.module;

import java.util.List;

import stencil.legend.module.LegendData.OpType;

public interface FacetData {
	/**What is the name of this facet?*/
	public String getName();
	
	/**What is the return tuple field set?*/
	public List<String> tupleFields();
	
	/**What type is the given facet?*/
	public OpType getFacetType();
}
