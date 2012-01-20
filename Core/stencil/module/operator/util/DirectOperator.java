package stencil.module.operator.util;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.util.OperatorData;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;

/**Operator that is its own invokeable. 
 * 
 * This is a (common) optimization case that can be used for functions
 * that saves some of the reflective invocation costs.
 * 
 * This class assumes that the return value of Invoke is not a tuple.
 * If this is not the case, any extending class must override TupleInvoke as well as the abstract methods.
 */
public abstract class DirectOperator implements StencilOperator, Invokeable {
	private OperatorData od;
	protected DirectOperator(OperatorData od) {this.od =od;}
	
	@Override
	public StencilOperator duplicate() throws UnsupportedOperationException {return this;}

	@Override
	public Invokeable getFacet(String facet) throws UnknownFacetException {return this;}

	@Override
	public String getName() {return od.name();}

	@Override
	public OperatorData getOperatorData() {return od;}

	@Override
	public DirectOperator viewpoint() {return this;}
	
	@Override
	public Tuple tupleInvoke(Object[] arguments)
			throws MethodInvokeFailedException {
		return new ArrayTuple(invoke(arguments));
	}
	
	@Override
	public String targetIdentifier() {return getName();}
}
