package stencil.operator.module.util;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import stencil.operator.module.FacetData;
import stencil.operator.module.OperatorData;
import stencil.parser.tree.Specializer;

public class MutableOperatorData implements OperatorData {
	protected Map<String, FacetData> facetData = new HashMap<String, FacetData>();
	protected Map<String, String> attributes = new HashMap<String, String>();
	protected String module;
	protected String operatorName;
	protected Specializer defaultSpecializer;
	
	public MutableOperatorData(OperatorData basis) {
		this.defaultSpecializer = basis.getDefaultSpecializer();
		this.module = basis.getModule();
		this.operatorName = basis.getName();
		
		for (String facet: basis.getFacets()) {
			facetData.put(facet, basis.getFacetData(facet));
		}
		
		for (String att: basis.getAttributes()) {
			attributes.put(att, basis.getAttribute(att));
		}
	}
	
	public MutableOperatorData(String module, String name, Specializer defaultSpecializer) {
		assert module != null : "Module cannot be null";
		assert name != null : "Name cannot be null";
		
		this.defaultSpecializer = defaultSpecializer;
		this.module =module;
		operatorName =name;
	}
	
	public void addFacet(FacetData data) {
		assert data != null : "Facet data object cannot be null";
		facetData.put(data.getName(), data);
	}
	
	/**Ensures that the named facet is not longer known by this legend-data object.
	 * If the facet was not known, no exception is thrown.
	 */
	public void removeFacet(String name) {if (facetData.containsKey(name)) {facetData.remove(name);}}
	
	public FacetData getFacetData(String name) {
		if (!facetData.containsKey(name)) {throw new IllegalArgumentException(String.format("Facet '%1$s' not know in operator '%2$s'.", name, getName()));}
		
		return facetData.get(name);
	}
	public Collection<String> getFacets() {return facetData.keySet();}

	public String getModule() {return module;}
 	public String getName() {return operatorName;}
 	public void setName(String operatorName) {this.operatorName = operatorName;}

 	public String getAttribute(String name) {return attributes.get(name);}
 	public void addAttribute(String name, String value) {attributes.put(name, value);}
 	public Collection<String> getAttributes() {return attributes.keySet();}
 	
	/**True as long as at least one facet is defined.*/
	public boolean isComplete() {return facetData.size()>0;}

	public Specializer getDefaultSpecializer() {return defaultSpecializer;}
}
