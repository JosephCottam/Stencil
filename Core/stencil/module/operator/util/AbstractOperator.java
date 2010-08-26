package stencil.module.operator.util;

import java.lang.reflect.Method;

import static java.lang.String.format;

import stencil.module.operator.StencilOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;

public abstract class AbstractOperator implements StencilOperator, Cloneable {
	protected final OperatorData operatorData;
	protected int stateID = Integer.MIN_VALUE;
	
	protected AbstractOperator(OperatorData opData) {
		this.operatorData = opData;
	}

	/**Naive implementation of getFacet.
	 * 
	 * Searches member methods of this instance and returns
	 * the one that matches (case insensitive) the name of the
	 * facet.
	 */
	public Invokeable getFacet(String name) {
		FacetData fd = operatorData.getFacet(name);
		String searchName = fd.getTarget().toUpperCase();
		for (Method method: this.getClass().getMethods()) {
			if (method.getName().toUpperCase().equals(searchName)) {
				return new ReflectiveInvokeable(method, this);
			}
		}
		throw new IllegalArgumentException(format("Could not find method named %1$s.", name));
	}
	
	public OperatorData getOperatorData() {return operatorData;}	
	public String getName() {return operatorData.getName();}

	public int stateID() {return stateID;}
	
	/**Unsupported operation in BasicProject, must be supplied by the 
	 * actual implementation.
	 */
	public StencilOperator duplicate() {throw new UnsupportedOperationException();}

	/**Default viewpoint creation is to just clone the underlying operator.
	 * This is sufficient if the operator is state-less or only value-state is
	 * retained.  If reference state is retained, it may still be sufficient, provided
	 * the referenced object and everything it transitively refers to is immutable.
	 * Otherwise, care must be taken to create a proper viewPoint.
	 **/
	public StencilOperator viewPoint() {
		try {return (StencilOperator) this.clone();}
		catch (Exception e) {throw new RuntimeException("Error creating viewPoint.", e);}
	}
}
