package stencil.module.operator.util;

import java.lang.reflect.Method;
import static java.lang.String.format;

import stencil.module.operator.StencilOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;

public abstract class BasicProject implements StencilOperator {
	protected final OperatorData operatorData;
	protected int stateID = Integer.MIN_VALUE;
	
	protected BasicProject(OperatorData opData) {
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

	public int StateID() {return stateID;}
	
	/**Unsupported operation in BasicProject, must be supplied by the 
	 * actual implementation.
	 */
	public StencilOperator duplicate() {throw new UnsupportedOperationException();}
}
