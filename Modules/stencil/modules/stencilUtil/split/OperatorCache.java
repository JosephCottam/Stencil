package stencil.modules.stencilUtil.split;

import stencil.module.operator.StencilOperator;

public interface OperatorCache {
	public static final Object[] EMPTY_ARGS = new Object[0];

	/**Invoke the facet on the operator with the given arg.**/
	public Object querySplit(StencilOperator op, String facet, Object[] args);

	
	/**Invoke the facet on the operator with the given arg.**/
	public Object doSplit(StencilOperator op, String facet, Object[] args);
	
	/**Track how often the passed operator OR operator cache itself have changed state.**/
	public int stateID();
	
	public OperatorCache viewpoint();
}
