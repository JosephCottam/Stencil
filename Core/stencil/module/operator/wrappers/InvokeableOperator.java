package stencil.module.operator.wrappers;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.String.format;

import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.OperatorData;
import stencil.tuple.Tuple;
import stencil.util.collections.KeysetConstantMap;


/**Associates a list of invokeables with corresponding facet names.
 * This class is used to build an operator out of static methods.
 * Duplicate is NOT supported if the method indicate is not a function.
 */
public final class InvokeableOperator extends AbstractOperator {
	private final Map<String, ReflectiveInvokeable> facets;
	
	/**All facets are directed towards the same target (a common case for functional operators).*/
	public InvokeableOperator(OperatorData opData, Invokeable target) {
		this(opData, new KeysetConstantMap(opData.getFacetNames(), target));
	}

	
	public InvokeableOperator(OperatorData opData, Map<String, ReflectiveInvokeable> facets) {
		super(opData);
		this.facets = facets;
		verify();
	}
	
	public InvokeableOperator(OperatorData opData, List<ReflectiveInvokeable> targets) {
		super(opData);
		this.facets = new HashMap();

		List<String> facetNames = opData.getFacetNames();
		assert facetNames.size() == targets.size() : "Facet list and targets list must be of the same length";
		
		for (int i=0; i< facetNames.size(); i++) {
			String facet = facetNames.get(i);
			ReflectiveInvokeable target = targets.get(i);
			if (Tuple.class.isAssignableFrom(target.getMethod().getReturnType())) {
				//TODO: Remove when the facets are re-written to allow more liberal return types.
				throw new RuntimeException("Facets provided must return a tuple.");
			}			
			this.facets.put(facet, target);
		}
		verify();
	}
	
	/**Verifies that all operators listed in opData are associated with an invokeable.
	 * Throws a runtime exception if there is anything missing; this should be run
	 * anytime the facets collection or operator data object changes.
	 */
	private void verify() {
		for (String facet: operatorData.getFacetNames()) {
			if (!facets.containsKey(facet)) {
				throw new RuntimeException(format("No invokeable was set for operator %1$s.", facet));
			}
		}
	}
		
	public String getName() {return operatorData.getName();}
	
	//TODO: Would it be better to see if all facets were functions as a quick-check?
	//TODO: implement a more general duplicate
	public InvokeableOperator duplicate() {
		boolean allStatic = true;
		for (ReflectiveInvokeable i: facets.values()) {
			if (!i.isStatic()) {allStatic = false; break;}
		}
		if (allStatic) {return this;}
		throw new UnsupportedOperationException();
	}

	public Invokeable getFacet(String facet) {
		Invokeable result = facets.get(facet);
		if (result == null) {throw new UnknownFacetException(getName(), facet, facets.keySet());}
		return result;
	}

	public OperatorData getOperatorData() {return operatorData;}
	
}

