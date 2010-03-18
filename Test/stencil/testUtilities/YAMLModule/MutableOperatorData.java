package stencil.testUtilities.YAMLModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.parser.tree.Specializer;

public class MutableOperatorData {
	private Specializer spec;
	private String name;
	private String target;
	private String module;
	private Map<String, MutableFacetData> facets = new HashMap();

	public void setDefaultSpecializer(Specializer spec) {this.spec = spec;}
	public Specializer getDefaultSpecializer() {return spec;}

	public void addFacet(MutableFacetData facet) {facets.put(facet.getName(), facet);}
	public MutableFacetData getFacet(String name) {
		if (facets.containsKey(name)) {return facets.get(name);}
		throw new IllegalArgumentException(String.format("Could not find find facet %1$s in operator %2$s.", name, this.name));
	}
	
	public List<MutableFacetData> getFacets() {return new ArrayList<MutableFacetData>(facets.values());}
	public void setFacets(List<MutableFacetData> newFacets) {
		facets.clear();
		for (MutableFacetData facet: newFacets) {addFacet(facet);}		
	}
	

	public String getModule() {return module;}
	public void setModule(String module) {this.module = module;}

	public void setName(String name) {this.name = name;}
	public String getName() {return name;}

	public String getTarget() {return target;}
	public void setTarget(String target) {this.target = target;}
	
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	/**Apply default values to any fields still unset.**/
	void mergeWith(MutableOperatorData defaults) {
		if (spec == null) {spec = defaults.getDefaultSpecializer();}
		if (name == null) {name = defaults.getName();}
		if (target == null) {target = name;} //The default target is the method/class that shares its name			
		if (facets.size() == 0) {setFacets(defaults.getFacets());}
		module = defaults.getModule(); //Just set the module name because this method is part of joining an operator to a module
	}
}
