package stencil.module.util;

import java.util.*;

import stencil.interpreter.tree.Specializer;

public final class OperatorData {
	public static final String HIGHER_ORDER_TAG = "HIGHER_ORDER";
	
	private Specializer spec;		 /**Default Specializer*/
	private String name;			 /**Operator Name*/
	
	//TODO: Make this a real class or method (not just a string pointing at one)
	private String target;			 /**Method or class to use for the given operator (containing class is determined by the module).*/
	private String module;		     /**Module this operator belongs to.*/
	private String description;   	 /**Text description of the operator.*/
	private Set<String> tags = new HashSet(); /**Extra flags used in further optimization**/
	private Map<String, FacetData> facets = new HashMap();
	
	public OperatorData() {}
	
	public OperatorData(String module, String name, Specializer specializer, String target, String... tags) {
		this.module = module;
		this.name = name;
		this.spec = specializer;
		this.target = target;
		this.tags.addAll(Arrays.asList(tags));
	}
	
	public OperatorData(OperatorData source) {
		spec = source.spec;
		name = source.name;
		target = source.target;
		module = source.module;
		description = source.description;
		tags.addAll(source.tags);
		for (FacetData facet: source.facets.values()) {
			addFacet(new FacetData(facet));
		}
	}
	
	public void setDefaultSpecializer(Specializer spec) {this.spec = spec;}
	public Specializer getDefaultSpecializer() {return spec;}
	
	public boolean hasTag(String name) {return tags.contains(name);}
	public void addTag(String name) {tags.add(name);}
	public void removeTag(String name) {tags.remove(name);}
	
	//TODO: Change addFacet to taken an array of FacetData objects
	public void addFacet(FacetData facet) {facets.put(facet.getName(), facet);}
	public FacetData getFacet(String name) {
		if (facets.containsKey(name)) {return facets.get(name);}
		throw new IllegalArgumentException(String.format("Could not find facet '%1$s' in operator %2$s.", name, this.name));
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
		       && module != null;
	}

	/**Apply default values to any fields still unset.**/
	void mergeWith(OperatorData defaults) {
		if (spec == null) {spec = defaults.getDefaultSpecializer();}
		if (name == null) {name = defaults.getName();}
		if (target == null) {target = name;} //The default target is the method/class that shares its name			
		if (facets.size() == 0) {setFacets(defaults.getFacets());}
		tags.addAll(defaults.tags);
		module = defaults.getModule(); //Just set the module name because this method is part of joining an operator to a module
	}
}
