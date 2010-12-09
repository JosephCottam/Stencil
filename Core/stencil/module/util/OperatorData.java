package stencil.module.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.parser.tree.Specializer;

public final class OperatorData {
	private Specializer spec;		 /**Default Specializer*/
	private String name;			 /**Operator Name*/
	private String target;			 /**Method or class to use for the given operator (containing class is determined by the module).*/
	private String module;		     /**Module this operator belongs to.*/
	private String description;   	 /**Text description of the operator.*/
	private Map<String, FacetData> facets = new HashMap();
	
	
	public OperatorData() {}
	
	public OperatorData(String module, String name, Specializer specializer) {
		this.module = module;
		this.name = name;
		this.spec = specializer;
	}
	
	public OperatorData(OperatorData source) {
		spec = source.spec;
		name = source.name;
		target = source.target;
		module = source.module;
		description = source.description;
		for (FacetData facet: source.facets.values()) {
			addFacet(new FacetData(facet));
		}
	}
	
	public void setDefaultSpecializer(Specializer spec) {this.spec = spec;}
	public Specializer getDefaultSpecializer() {return spec;}

	public void addFacet(FacetData facet) {facets.put(facet.getName(), facet);}
	public FacetData getFacet(String name) {
		if (facets.containsKey(name)) {return facets.get(name);}
		throw new IllegalArgumentException(String.format("Could not find find facet '%1$s' in operator %2$s.", name, this.name));
	}
	
	public boolean hasFacet(String name) {return getFacetNames().contains(name);}
	public List<String> getFacetNames() {return new ArrayList(facets.keySet());}
	public List<FacetData> getFacets() {return new ArrayList(facets.values());}
	public void setFacets(List<FacetData> newFacets) {
		facets.clear();
		for (FacetData facet: newFacets) {addFacet(facet);}		
	}
	

	public String getModule() {return module;}
	public void setModule(String module) {this.module = module;}

	public void setName(String name) {this.name = name;}
	public String getName() {return name;}

	public String getTarget() {return target;}
	public void setTarget(String target) {this.target = target;}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	
	public boolean isComplete() {
		return target != null
			   && spec != null 
		       && name != null 
		       && module != null
		       && facets.size() >0;
	}

	/**Apply default values to any fields still unset.**/
	void mergeWith(OperatorData defaults) {
		if (spec == null) {spec = defaults.getDefaultSpecializer();}
		if (name == null) {name = defaults.getName();}
		if (target == null) {target = name;} //The default target is the method/class that shares its name			
		if (facets.size() == 0) {setFacets(defaults.getFacets());}
		module = defaults.getModule(); //Just set the module name because this method is part of joining an operator to a module
	}
}
