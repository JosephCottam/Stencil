package stencil.operator.wrappers;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.String.format;

import stencil.operator.module.OperatorData;
import stencil.operator.util.BasicProject;
import stencil.operator.util.Invokeable;
import stencil.operator.util.ReflectiveInvokeable;
import stencil.operator.util.UnknownFacetException;
import stencil.tuple.Tuple;
import stencil.util.collections.KeysetConstantMap;


/**Associates a list of invokeables with corresponding 
 * facet names.  Duplicate is NOT supported for non-functions.
 * 
 * TODO: Add support for duplicate.  Have the underlying
 * object of a facet support duplicate...
 */
public final class InvokeablesLegend extends BasicProject {
	private final String name;
	private final Map<String, ReflectiveInvokeable> facets;
	
	/**All facets are directed towards the same target (a common case for functional operators).*/
	public InvokeablesLegend(String name, OperatorData opData, Invokeable target) {
		this(name, opData, new KeysetConstantMap(opData.getFacets(), target));
	}

	
	public InvokeablesLegend(String name, OperatorData opData, Map<String, ReflectiveInvokeable> facets) {
		super(opData);
		this.name= name;
		this.facets = facets;
		verify();
	}
	
	public InvokeablesLegend(String name, OperatorData opData, List<ReflectiveInvokeable> targets) {
		super(opData);
		this.name = name;
		this.facets = new HashMap();

		List<String> facetNames = opData.getFacets();
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
		for (String facet: operatorData.getFacets()) {
			if (!facets.containsKey(facet)) {
				throw new RuntimeException(format("No invokeable was set for operator %1$s.", facet));
			}
		}
	}
		
	public String getName() {return name;}
	
	//TODO: Would it be better to see if all facets were functions as a quick-check?
	//TODO: implement a more general duplicate
	public InvokeablesLegend duplicate() {
		boolean allStatic = true;
		for (ReflectiveInvokeable i: facets.values()) {
			if (!i.isStatic()) {allStatic = false; break;}
		}
		if (allStatic) {return this;}
		throw new UnsupportedOperationException();
	}

	public Invokeable getFacet(String facet) {
		Invokeable result = facets.get(facet);
		if (result == null) {throw new UnknownFacetException(name, facet, facets.keySet());}
		return result;
	}

	public OperatorData getOperatorData() {return operatorData;}
	
}

